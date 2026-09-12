package com.xiaolin.shared.infra.dao

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.annotation.Version
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

data class PropAndColumn(
    /** 实体属性名（驼峰），同时是 SQL 占位符名 */
    val prop: String,
    /** 数据库列名（已转换，勿再转一次） */
    val column: String,
    /** @SqlType 指定的 SQL 类型，null 表示交给驱动推断 */
    val type: Int? = null,
    /** @ReadOnlyProperty：不参与 INSERT / UPDATE */
    val readOnly: Boolean = false,
    /** @InsertOnlyProperty：只参与 INSERT */
    val insertOnly: Boolean = false,
)

abstract class AbstractJdbcDao<T : Any>(
    protected val jdbc: NamedParameterJdbcTemplate,
    protected val entityClass: KClass<T>,
) {
    // ---------------- 子类契约 ----------------
    protected abstract val rowMapper: RowMapper<T>

    /**
     * id 是否参与 INSERT。
     * true  = id 由应用侧生成（雪花 / UUID），必须写进 INSERT  —— 默认值
     * false = id 由数据库生成（BIGSERIAL / GENERATED ALWAYS AS IDENTITY）
     */
    protected open val idInInsert: Boolean get() = true

    /**
     * 是否给表名 / 列名加双引号。
     *
     * 默认 false：PostgreSQL 会把未加引号的标识符折叠为小写，
     * 只要实体与 DDL 全部小写，无需引号。
     *
     * 以下情况必须开启（子类覆盖为 true）：
     *  1. 列名是 PG 保留字（order / user / rank / table 等）
     *  2. 列名含大写字母或特殊字符，且 DDL 里也用了双引号
     *
     * ⚠️ 开启后所有标识符都会被加引号。若 DDL 是 `CREATE TABLE Student`
     *    （实际落库为 student），加引号的 "Student" 反而找不到表 —— 两者必须一致。
     */
    protected open val quoteIdentifiers: Boolean get() = false

    protected fun quote(identifier: String): String {
        if (!quoteIdentifiers) return identifier
        return identifier.split('.').joinToString(".") { "\"$it\"" }
    }
    // ---------------- 元数据（每个 DAO 只解析一次） ----------------

    /**
     * 注解查找：property → field → getter 三处都查。
     *
     * 为什么必须这样写：
     *  - Kotlin 反射的 `findAnnotation` 只读取 **@property:** 落点的注解
     *  - Spring Data 的注解都是 Java 注解（`@Target = {FIELD, METHOD, ANNOTATION_TYPE}`，
     *    不含 PARAMETER），Kotlin 默认会把它放到 **幕后字段** 上
     *  - 于是 `prop.findAnnotation<Id>()` 返回 null → 报 "found 0"
     *
     * 三个落点分别对应写法：
     *  - `@property:Id`  → findAnnotation 命中
     *  - `@field:Id`     → javaField.getAnnotation 命中（Kotlin 默认落点）
     *  - `@get:Id`       → javaGetter.getAnnotation 命中
     */
    private inline fun <reified A : Annotation> KProperty1<T, *>.ann(): A? =
        findAnnotation<A>()
            ?: javaField?.getAnnotation(A::class.java)
            ?: javaGetter?.getAnnotation(A::class.java)

    private inline fun <reified A : Annotation> KProperty1<T, *>.hasAnn(): Boolean =
        ann<A>() != null

    /** 属性注解诊断信息（用于报错时定位落点） */
    private fun KProperty1<T, *>.annDump(): String {
        val f = javaField?.annotations?.joinToString { "@" + it.annotationClass.simpleName }
        val g = javaGetter?.annotations?.joinToString { "@" + it.annotationClass.simpleName }
        val p = annotations.joinToString { "@" + it.annotationClass.simpleName }
        return "property=[$p] field=[$f] getter=[$g]"
    }

    /**
     * 持久化属性：主构造参数优先（保证声明顺序），
     * 再补上父类属性（memberProperties 含父类，primaryConstructor 不含）。
     * 排除计算属性 —— 它们没有幕后字段。
     */
    private val props: List<KProperty1<T, *>> by lazy {
        val properties = entityClass.memberProperties.filter { it.javaField != null }
        val byName = properties.associateBy { it.name }
        val ctorNames = entityClass.primaryConstructor?.parameters?.mapNotNull { it.name } ?: emptyList()
        val inheritedNames = properties
            .map { it.name }
            .filter { it !in ctorNames }
        (ctorNames + inheritedNames)
            .mapNotNull { byName[it] }
    }

    protected open val allProps: List<PropAndColumn> by lazy {
        val list = props.map(::toPropAndColumn)
        val dup = list.groupBy { it.column }.filterValues { it.size > 1 }.keys
        require(dup.isEmpty()) {
            "Duplicate column names in ${entityClass.simpleName}: $dup. " +
                    "Check @Column values and camelToSnake collisions (e.g. userName vs user_name)."
        }
        list
    }

    /** 属性访问器缓存，避免 toParams 里重复做 map 查找 */
    private val accessors: List<Pair<PropAndColumn, KProperty1<T, *>>> by lazy {
        val byName = props.associateBy { it.name }
        allProps.mapNotNull { pc -> byName[pc.prop]?.let { pc to it } }
    }

    protected open val tableName: String by lazy { resolveTableName() }
    protected open val idProp: String by lazy { resolveIdProp() }
    protected open val idColumn: String by lazy {
        allProps.firstOrNull { it.prop == idProp }?.column ?: col(idProp)
    }
    protected open val versionProp: String? by lazy { resolveVersionProp() }

    /** versionProp 为 null（实体无 @Version）时恒为 false，即不做任何排除 */
    private fun isVersion(pc: PropAndColumn): Boolean =
        versionProp != null && pc.prop == versionProp

    protected open val insertProps: List<PropAndColumn>
        get() = allProps.filter {
            (idInInsert || it.prop != idProp) && !isVersion(it) && !it.readOnly
        }

    protected open val updateProps: List<PropAndColumn>
        get() = allProps.filter {
            it.prop != idProp && !isVersion(it) && !it.readOnly && !it.insertOnly
        }

    // ---------------- SQL ----------------

    protected open val insertSql: String by lazy {
        require(insertProps.isNotEmpty()) { "No insertable columns for ${entityClass.simpleName}" }
        "INSERT INTO ${quote(tableName)} (${insertProps.joinToString(", ") { quote(it.column) }}) " +
                "VALUES (${holders(insertProps.map { it.prop })})"
    }

    protected open val updateSql: String by lazy {
        val set = updateProps.map { "${quote(it.column)} = :${it.prop}" } +
                listOfNotNull(versionProp?.let { v ->
                    val c = quote(columnOf(v))
                    "$c = $c + 1"
                })
        require(set.isNotEmpty()) { "No updatable columns for ${entityClass.simpleName}" }
        "UPDATE ${quote(tableName)} SET ${set.joinToString(", ")}"
    }

    // ---------------- 元数据解析 ----------------

    /**
     * @Table 的 value 与 name 是 @AliasFor 互别名，两个都读。
     * 注意：@Table 有 name，@Column 没有（只有 value），两者规则不同。
     */
    private fun resolveTableName(): String {
        val t = entityClass.findAnnotation<Table>()
        val raw = t?.let { it.value.takeIf(String::isNotBlank) ?: it.name.takeIf(String::isNotBlank) }
        return raw ?: camelToSnake(
            entityClass.simpleName
                ?: error("TableName not found for ${entityClass.qualifiedName}")
        )
    }

    /** @Column 只有 value 这一个属性 */
    private fun toPropAndColumn(property: KProperty1<T, *>): PropAndColumn {
        val a = property.ann<Column>()
        val column = a?.value?.takeIf { it.isNotBlank() } ?: col(property.name)
        return PropAndColumn(
            prop       = property.name,
            column     = column,
            type       = property.ann<SqlType>()?.value,
            readOnly   = property.hasAnn<ReadOnlyProperty>(),
            insertOnly = property.hasAnn<InsertOnlyProperty>(),
        )
    }

    private val idProperty: KProperty1<T, *> by lazy {
        val ids = props.filter { it.hasAnn<Id>() }
        require(ids.size == 1) {
            val dump = entityClass.memberProperties.joinToString("\n") { p ->
                "    - ${p.name}: ${p.annDump()}"
            }
            "Exactly one @Id required in ${entityClass.qualifiedName}, found ${ids.size}.\n" +
                    "排查:\n" +
                    "  1. @Id 是否在父类(BaseEntity)的属性上？子类主构造里无 val 的 id 只是传参，不是属性；\n" +
                    "  2. 建议显式写 use-site target: @field:Id open val id: Long? = null\n" +
                    "     （Kotlin 默认把 Java 注解放到幕后字段，findAnnotation 只读 property 落点）\n" +
                    "  3. 扫描到的属性及其注解落点:\n$dump"
        }
        ids[0]
    }

    private fun resolveIdProp(): String = idProperty.name

    /**
     * 解析乐观锁属性名。
     *
     * 实体无 @Version（表也无 version 列）时返回 null，DAO 全程按「无锁模式」工作：
     *   - insertProps / updateProps 不做 version 排除
     *   - updateSql 不加 `version = version + 1`
     *   - update() 的 WHERE 不带版本条件，0 行行为由 [failOnZeroUpdate] 决定
     *   - upsert 的 DO UPDATE SET 不含 version
     */
    private fun resolveVersionProp(): String? {
        val vs = props.filter { it.hasAnn<Version>() }
        require(vs.size <= 1) {
            "At most one @Version in ${entityClass.qualifiedName}, found ${vs.size}"
        }
        return vs.firstOrNull()?.name
    }

    private fun columnOf(prop: String?): String =
        prop?.let { name -> allProps.firstOrNull { it.prop == name }?.column ?: col(name) }
            ?: error("Property name is null")

    // ---------------- 写入 ----------------

    /** 实体 → 参数源。null 也必须注册，否则占位符缺参 */
    protected open fun toParams(entity: T): MapSqlParameterSource =
        MapSqlParameterSource().apply {
            accessors.forEach { (pc, prop) ->
                val raw = prop.getter.call(entity)
                // 枚举 → name。驱动不认识任意 Java 枚举，会抛
                // "Can't infer the SQL type to use for an instance of ..."
                val isEnum = raw is Enum<*>
                val value = if (isEnum) raw.name else raw
                // 枚举走 setString 即可；不要给 String 注册 Types.OTHER，
                // 那是给 PGobject 用的，两者不兼容
                if (!isEnum && pc.type != null) addValue(pc.prop, value, pc.type)
                else addValue(pc.prop, value)
            }
        }

    open fun insertReturningId(entity: T): Long =
        insertReturning(entity, Long::class.java)

    open fun <R : Any> insertReturning(entity: T, type: Class<R>): R =
        jdbc.queryFirstColumn("$insertSql RETURNING $idColumn", toParams(entity), type)
            ?: error("INSERT ... RETURNING returned no row: $tableName")

    open fun insert(entity: T): Int = jdbc.update(insertSql, toParams(entity))

    open fun insertBatch(entities: List<T>, batchSize: Int = 500): IntArray {
        if (entities.isEmpty()) return IntArray(0)
        return batch(insertSql, entities.map(::toParams).toTypedArray(), batchSize)
    }

    /**
     * 无 @Version 时，update 影响 0 行是否抛异常。
     *
     * 默认 false（静默返回 0）：
     *   - 表无 version 列时，幂等更新 / 条件不满足导致的 0 行是正常业务结果
     *   - 保持与 JdbcTemplate 一致的行为，调用方按需判断返回值
     *
     * 设为 true：0 行时抛 EmptyResultDataAccessException，
     *   适合「更新必须生效」的场景（如状态机推进）。
     *
     * 注意：有 @Version 时，0 行恒抛 OptimisticLockingFailureException，
     *       与本开关无关 —— 那是并发冲突的强信号，不能静默。
     */
    protected open val failOnZeroUpdate: Boolean get() = false

    /**
     * 按主键更新。
     *
     * version 敏感逻辑：
     *  - 有 @Version：SET 带自增 + WHERE 带版本条件，0 行抛乐观锁异常
     *  - 无 @Version：SET 无自增 + WHERE 只有主键，0 行行为由 [failOnZeroUpdate] 决定
     */
    open fun update(entity: T): Int {
        val where = buildString {
            append("${quote(idColumn)} = :$idProp")
            versionProp?.let { v -> append(" AND ${quote(columnOf(v))} = :$v") }
        }
        val n = jdbc.update("$updateSql WHERE $where", toParams(entity))
        if (n != 0) return n

        val vp = versionProp
        if (vp != null) {
            throw OptimisticLockingFailureException(
                "Update affected 0 rows in $tableName (WHERE $where). " +
                        "Row missing, $vp is stale, or $vp is null."
            )
        }
        if (failOnZeroUpdate) {
            throw EmptyResultDataAccessException(
                "Update affected 0 rows in $tableName (WHERE $where). Row missing. " +
                        "Set failOnZeroUpdate=false to return 0 instead.", 1
            )
        }
        return 0
    }

    /** PG upsert：ON CONFLICT DO UPDATE */
    open fun upsert(entity: T, conflictTarget: String): Int =
        jdbc.update(upsertSql(conflictTarget), toParams(entity))

    open fun upsertBatch(
        entities: List<T>,
        conflictTarget: String,
        batchSize: Int = 500,
    ): IntArray {
        if (entities.isEmpty()) return IntArray(0)
        return batch(upsertSql(conflictTarget), entities.map(::toParams).toTypedArray(), batchSize)
    }
    private fun upsertSql(conflictTarget: String): String {
        val updates = updateProps.joinToString(", ") {
            "${quote(it.column)} = EXCLUDED.${quote(it.column)}"
        }
        val target = conflictTarget.split(',').joinToString(", ") { quote(it.trim()) }
        return "$insertSql ON CONFLICT ($target) DO UPDATE SET $updates"
    }

    private fun batch(
        sql: String,
        args: Array<MapSqlParameterSource>,
        batchSize: Int,
    ): IntArray {
        val result = IntArray(args.size)
        var from = 0
        var done = 0
        while (from < args.size) {
            val to = minOf(from + batchSize, args.size)
            val part = jdbc.batchUpdate(sql, args.copyOfRange(from, to))
            part.copyInto(result, done)
            done += part.size
            from = to
        }
        return result
    }

    open fun findById(id: Any): T? = jdbc.query(
        "SELECT * FROM ${quote(tableName)} WHERE ${quote(idColumn)} = :$idProp",
        MapSqlParameterSource(idProp, id), rowMapper
    ).firstOrNull()

    open fun deleteById(id: Any): Int = jdbc.update(
        "DELETE FROM ${quote(tableName)} WHERE ${quote(idColumn)} = :$idProp",
        MapSqlParameterSource(idProp, id)
    )

    // ---------------- 查询 ----------------

    fun query(sql: String, params: SqlParameterSource = MapSqlParameterSource()): List<T> =
        jdbc.query(sql, params, rowMapper) ?: emptyList()

    fun queryOne(sql: String, params: SqlParameterSource = MapSqlParameterSource()): T? =
        query(sql, params).firstOrNull()

    fun count(sql: String, params: SqlParameterSource = MapSqlParameterSource()): Long =
        jdbc.queryForObject(sql, params, Long::class.java) ?: 0L

    fun execute(sql: String, params: SqlParameterSource = MapSqlParameterSource()): Int =
        jdbc.update(sql, params)

    /**
     * 分页。countSql 建议业务自己给，自动包子查询会让 PG 放弃部分索引优化。
     * params 必须是 MapSqlParameterSource —— 否则无法保留 sqlType。
     */
    fun queryPage(
        sql: String,
        countSql: String,
        params: MapSqlParameterSource,
        pageable: Pageable,
    ): PageImpl<T> {
        val total = count(countSql, params)
        if (total == 0L) return PageImpl(emptyList(), pageable, 0)
        if (pageable.isUnpaged) return PageImpl(query(sql, params), pageable, total)
        val ps = MapSqlParameterSource().apply {
            params.parameterNames.forEach { n ->
                addValue(n, params.getValue(n), params.getSqlType(n))
            }
            addValue("__limit", pageable.pageSize)
            addValue("__offset", pageable.offset)
        }
        return PageImpl(query("$sql LIMIT :__limit OFFSET :__offset", ps), pageable, total)
    }

    // ---------------- 工具 ----------------
    protected fun col(prop: String): String = camelToSnake(prop)
    protected fun holders(props: List<String>) = props.joinToString(", ") { ":$it" }

    /** 取实体的主键值 */
    protected fun idOf(entity: T): Any? =
        accessors.firstOrNull { (pc, _) -> pc.prop == idProp }?.second?.getter?.call(entity)
}
