package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.Role
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class RoleDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<Role>(jdbc, Role::class) {

    override val rowMapper: RowMapper<Role> = rowMapperOf { rs ->
        Role(
            id = rs.getLong("id"),
            tenantId = rs.getLong("tenant_id"),
            appId = rs.getLong("app_id"),
            parentId = rs.getLong("parent_id"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            isTenantAdmin = rs.getBoolean("is_tenant_admin"),
            builtin = rs.getBoolean("builtin"),
            status = rs.getInt("status"),
            sortNo = rs.getInt("sort_no"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getLong("created_by"),
            updatedBy = rs.getLong("updated_by"),
            deletedBy = rs.getLong("deleted_by"),
            deleted = rs.getBoolean("deleted"),
        )
    }

    fun findByTenantAppAndCode(tenantId: Long, appId: Long, code: String): Role? = queryOne(
        sql = """
            SELECT * FROM sys.role
             WHERE tenant_id = :tenantId AND app_id = :appId AND code = :code AND deleted = false
        """.trimIndent(),
        params = paramsOf("tenantId" to tenantId, "appId" to appId, "code" to code),
    )

    fun existsByTenantAppAndCode(tenantId: Long, appId: Long, code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            """
            SELECT COUNT(*) FROM sys.role
             WHERE tenant_id = :tenantId AND app_id = :appId AND code = :code AND deleted = false
            """.trimIndent()
        } else {
            """
            SELECT COUNT(*) FROM sys.role
             WHERE tenant_id = :tenantId AND app_id = :appId AND code = :code
               AND id <> :excludeId AND deleted = false
            """.trimIndent()
        }
        return count(
            sql,
            paramsOf("tenantId" to tenantId, "appId" to appId, "code" to code, "excludeId" to excludeId),
        ) > 0
    }

    fun findByParentId(tenantId: Long, parentId: Long): List<Role> = query(
        sql = "SELECT * FROM sys.role WHERE tenant_id = :tenantId AND parent_id = :parentId AND deleted = false ORDER BY sort_no, id",
        params = paramsOf("tenantId" to tenantId, "parentId" to parentId),
    )

    fun countByParentId(tenantId: Long, parentId: Long): Long = count(
        sql = "SELECT COUNT(*) FROM sys.role WHERE tenant_id = :tenantId AND parent_id = :parentId AND deleted = false",
        params = paramsOf("tenantId" to tenantId, "parentId" to parentId),
    )
}
