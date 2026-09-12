//package com.xiaolin.shared.infra.interceptors
//
//import com.xiaolin.shared.common.page.PageParam
//import com.xiaolin.shared.common.page.PageList
//import org.apache.ibatis.executor.statement.StatementHandler
//import org.apache.ibatis.mapping.BoundSql
//import org.apache.ibatis.mapping.MappedStatement
//import org.apache.ibatis.mapping.SqlCommandType
//import org.apache.ibatis.plugin.*
//import org.apache.ibatis.reflection.MetaObject
//import org.apache.ibatis.reflection.SystemMetaObject
//import org.apache.ibatis.type.TypeHandler
//import java.sql.Connection
//import java.sql.PreparedStatement
//import java.sql.ResultSet
//import java.sql.Statement
//import java.util.*
//import kotlin.collections.get
//import org.apache.ibatis.session.ResultHandler as MyBatisResultHandler
//
//
//// ==================== 3. 参数查找工具 ====================
//
//object PageParamFinder {
//
//    fun find(parameterObject: Any?): PageParam? {
//        if (parameterObject == null) return null
//        if (parameterObject is PageParam) return parameterObject
//
//        // MyBatis 多参数时，参数会被包装成 Map
//        if (parameterObject is Map<*, *>) {
//            for (value in parameterObject.values) {
//                if (value is PageParam) return value
//            }
//        }
//
//        // 反射兜底：参数对象内部持有 PageParam 字段
//        return try {
//            val field = parameterObject.javaClass.declaredFields
//                .firstOrNull { PageParam::class.java.isAssignableFrom(it.type) }
//            field?.apply { isAccessible = true }?.get(parameterObject) as? PageParam
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
//
//// ==================== 4. COUNT SQL 执行器 ====================
//
//object CountSqlExecutor {
//
//    fun executeCount(
//        connection: Connection,
//        originalSql: String,
//        boundSql: BoundSql,
//        ms: MappedStatement
//    ): Long {
//        val countSql = buildCountSql(originalSql)
//        val stmt: PreparedStatement = connection.prepareStatement(countSql)
//
//        // 复用原始 SQL 的参数绑定
//        val parameterMappings = boundSql.parameterMappings
//        val parameterObject = boundSql.parameterObject
//
//        if (parameterMappings != null && parameterObject != null) {
//            val configuration = ms.configuration
//            val typeHandlerRegistry = configuration.typeHandlerRegistry
//
//            for (i in parameterMappings.indices) {
//                val mapping = parameterMappings[i]
//                val propertyName = mapping.property
//
//                val value = when {
//                    boundSql.hasAdditionalParameter(propertyName) ->
//                        boundSql.getAdditionalParameter(propertyName)
//                    parameterObject is Map<*, *> ->
//                        parameterObject[propertyName]
//                    else -> {
//                        val metaObject = SystemMetaObject.forObject(parameterObject)
//                        if (metaObject.hasGetter(propertyName)) metaObject.getValue(propertyName) else null
//                    }
//                }
//                @Suppress("UNCHECKED_CAST")
//                val typeHandler = (mapping.typeHandler
//                    ?: typeHandlerRegistry.getTypeHandler(mapping.javaType, mapping.jdbcType)
//                        ) as TypeHandler<Any?>
////                val typeHandler = mapping.typeHandler ?: typeHandlerRegistry
////                    .getTypeHandler(mapping.javaType, mapping.jdbcType)
//                typeHandler.setParameter(stmt, i + 1, value, mapping.jdbcType)
//            }
//        }
//
//        val rs: ResultSet = stmt.executeQuery()
//        val total = if (rs.next()) rs.getLong(1) else 0L
//
//        rs.close()
//        stmt.close()
//        return total
//    }
//
//    private fun buildCountSql(originalSql: String): String {
//        // 简单实现：包裹原始 SQL。生产环境建议用 JSqlParser 解析后移除 ORDER BY
//        return "SELECT COUNT(1) FROM ($originalSql) __page_count_tmp"
//    }
//}
//
//// ==================== 5. 核心拦截器 ====================
//
//@Intercepts(
//    Signature(
//        type = StatementHandler::class,
//        method = "query",
//        args = [Statement::class, MyBatisResultHandler::class]
//    )
//)
//class PageInterceptor : Interceptor {
//
//    override fun intercept(invocation: Invocation): Any? {
//        val handler = invocation.target as StatementHandler
//        val metaObject: MetaObject = SystemMetaObject.forObject(handler)
//
//        val ms: MappedStatement =
//            metaObject.getValue("delegate.mappedStatement") as MappedStatement
//
//        // 只拦截 SELECT
//        if (ms.sqlCommandType != SqlCommandType.SELECT) {
//            return invocation.proceed()
//        }
//
//        val parameterObject = handler.parameterHandler.parameterObject
//        val pageParam = PageParamFinder.find(parameterObject) ?: return invocation.proceed()
//
//        val connection = (invocation.args[0] as Statement).connection
//        val boundSql: BoundSql = handler.boundSql
//        val originalSql = boundSql.sql
//
//        // 1. 执行 COUNT
//        var total = 0L
//        if (pageParam.searchCount) {
//            total = CountSqlExecutor.executeCount(connection, originalSql, boundSql, ms)
//            if (total == 0L) {
//                // 总数为 0，跳过数据查询，直接返回空 PageList
//                val emptyResult = PageList<Any>(pageParam.pageSize, pageParam.pageNum)
//                emptyResult.total = 0
//                emptyResult.pageNum = pageParam.pageNum
//                emptyResult.pageSize = pageParam.pageSize
//                replaceResultHandler(invocation, emptyResult)
//                return emptyResult
//            }
//        }
//
//        // 2. 改写 SQL，拼接 LIMIT
//        val pageSql = "$originalSql LIMIT ${pageParam.pageSize} OFFSET ${pageParam.offset}"
//        metaObject.setValue("delegate.boundSql.sql", pageSql)
//
//        // 3. 执行分页查询
//        val result = invocation.proceed()
//
//        // 4. 将结果包装为 PageList
//        return if (result is List<*>) {
//            val pageList = PageList<Any>(pageParam.pageSize, pageParam.pageNum)
//            pageList.addAll(result as Collection<Any>)
//            pageList.total = total
//            pageList
//        } else {
//            result
//        }
//    }
//
//    /**
//     * 当 total=0 时，需要让 MyBatis 的 ResultHandler 感知到空结果，
//     * 避免上层拿到 null。
//     */
//    private fun replaceResultHandler(invocation: Invocation, emptyList: PageList<Any>) {
//        val resultHandler = invocation.args[1] as? MyBatisResultHandler<*> ?: return
//        // 不额外处理，proceed 会返回空列表，上层自然拿到空结果
//    }
//
//    override fun plugin(target: Any): Any {
//        return Plugin.wrap(target, this)
//    }
//
//    override fun setProperties(properties: Properties?) {
//        // 可扩展：读取数据库方言等配置
//    }
//}