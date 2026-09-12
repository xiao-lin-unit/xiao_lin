package com.xiaolin.shared.infra.dao

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import java.sql.ResultSet

/**
 * 显式声明属性的 JDBC SQL 类型。
 *
 * 用法：`@SqlType(java.sql.Types.OTHER)`
 *
 * 必须标 @SqlType（pgjdbc 无法从 Java 对象推断）：
 * jsonb / json → Types.OTHER + PGobject
 * uuid → Types.OTHER
 * text[] / int[] 等任意数组 → Types.ARRAY(2003)
 * PG 原生 enum → Types.OTHER + PGobject
 * inet / cidr / point 等 → Types.OTHER
 * 否则会报 `Can't infer the SQL type to use`。
 *
 * 无需标注（驱动或 Spring 原生支持）：
 * bytea、bool、text/varchar、int2/4/8、numeric、float4/8
 * date、time、timestamp(用 LocalDateTime)、timestamptz(用 OffsetDateTime)
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SqlType(val value: Int)

/** 需要显式指定 SQL 类型的值包装 */
data class Typed(val value: Any?, val sqlType: Int)

infix fun Any?.typedAs(sqlType: Int) = Typed(this, sqlType)

/** 构造参数源；Typed 值会自动带上 sqlType */
fun paramsOf(vararg pairs: Pair<String, Any?>): MapSqlParameterSource =
    MapSqlParameterSource().apply {
        for ((k, v) in pairs) {
            val raw = if (v is Typed) v.value else v
            val value = if (raw is Enum<*>) raw.name else raw
            if (v is Typed) addValue(k, value, v.sqlType)
            else addValue(k, value)
        }
    }

/** 合并两个参数源（保留 sqlType） */
fun MapSqlParameterSource.plus(other: MapSqlParameterSource): MapSqlParameterSource =
    MapSqlParameterSource().apply {
        other.parameterNames.forEach { n -> addValue(n, other.getValue(n), other.getSqlType(n)) }
        this@plus.parameterNames.forEach { n -> addValue(n, this@plus.getValue(n), this@plus.getSqlType(n)) }
    }

/** 驼峰 → 下划线，逐字符实现，避免 Kotlin 里 `$1` 转义的坑 */
fun camelToSnake(name: String): String {
    val sb = StringBuilder(name.length + 4)
    for (i in name.indices) {
        val c = name[i]
        if (c.isUpperCase()) {
            val prev = if (i > 0) name[i - 1] else null
            val next = if (i + 1 < name.length) name[i + 1] else null
            val needSep = i > 0 && (
                    (prev != null && !prev.isUpperCase()) || (next != null && next.isLowerCase())
                    )
            if (needSep) sb.append('_')
            sb.append(c.lowercaseChar())
        } else sb.append(c)
    }
    return sb.toString()
}

/**
 * 单行单列。0 行返回 null（不抛 EmptyResultDataAccessException）。
 *
 * 刻意不用 reified —— 返回值位置的 reified 在 Elvis 左侧、字符串模板里
 * 会推断失败（`Cannot infer type for type parameter 'R'`）。
 */
fun <R : Any> NamedParameterJdbcTemplate.queryFirstColumn(
    sql: String,
    params: SqlParameterSource,
    type: Class<R>,
): R? = query(sql, params) { rs, _ -> rs.getObject(1, type) }.firstOrNull()

/** 单行单列 Long，0 行抛异常 */
fun NamedParameterJdbcTemplate.queryForLong(sql: String, params: SqlParameterSource): Long =
    queryFirstColumn(sql, params, Long::class.java)
        ?: error("queryForLong got no row: $sql")

/** RowMapper 的 SAM 简写 */
inline fun <reified T : Any> rowMapperOf(crossinline f: (ResultSet) -> T): RowMapper<T> =
    RowMapper { rs, _ -> f(rs) }

