package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.TenantType
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class TenantTypeDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<TenantType>(jdbc, TenantType::class) {

    override val rowMapper: RowMapper<TenantType> = rowMapperOf { rs ->
        TenantType(
            id = rs.getLong("id"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            orgLevels = rs.getString("org_levels") ?: "[]",
            defaultApps = rs.getString("default_apps") ?: "[]",
            status = rs.getInt("status"),
            builtin = rs.getBoolean("builtin"),
            sortNo = rs.getInt("sort_no"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getObject("created_by", Long::class.java),
            updatedBy = rs.getObject("updated_by", Long::class.java),
            deletedBy = rs.getObject("deleted_by", Long::class.java),
            deleted = rs.getBoolean("deleted"),
        )
    }

    fun findByCode(code: String): TenantType? = queryOne(
        sql = "SELECT * FROM sys.tenant_type WHERE code = :code AND deleted = false",
        params = paramsOf("code" to code),
    )

    /** excludeId 用于更新时的唯一性校验，排除自身 */
    fun existsByCode(code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.tenant_type WHERE code = :code AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.tenant_type WHERE code = :code AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("code" to code, "excludeId" to excludeId)) > 0
    }
}
