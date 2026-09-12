package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.GlobalRole
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class GlobalRoleDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<GlobalRole>(jdbc, GlobalRole::class) {

    override val rowMapper: RowMapper<GlobalRole> = rowMapperOf { rs ->
        GlobalRole(
            id = rs.getLong("id"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            isSuper = rs.getBoolean("is_super"),
            builtin = rs.getBoolean("builtin"),
            status = rs.getInt("status"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getLong("created_by"),
            updatedBy = rs.getLong("updated_by"),
            deletedBy = rs.getLong("deleted_by"),
            deleted = rs.getBoolean("deleted"),
        )
    }

    fun findByCode(code: String): GlobalRole? = queryOne(
        sql = "SELECT * FROM sys.global_role WHERE code = :code AND deleted = false",
        params = paramsOf("code" to code),
    )

    fun existsByCode(code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.global_role WHERE code = :code AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.global_role WHERE code = :code AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("code" to code, "excludeId" to excludeId)) > 0
    }

    /** 超管角色，系统内应唯一 */
    fun findSuper(): GlobalRole? = queryOne(
        sql = "SELECT * FROM sys.global_role WHERE is_super = true AND deleted = false",
    )
}
