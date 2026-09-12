package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.IdentityType
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class IdentityTypeDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<IdentityType>(jdbc, IdentityType::class) {

    override val rowMapper: RowMapper<IdentityType> = rowMapperOf { rs ->
        IdentityType(
            id = rs.getLong("id"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            tenantTypeId = rs.getLong("tenant_type_id"),
            description = rs.getString("description"),
            builtin = rs.getBoolean("builtin"),
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

    fun findByCode(code: String): IdentityType? = queryOne(
        sql = "SELECT * FROM sys.identity_type WHERE code = :code AND deleted = false",
        params = paramsOf("code" to code),
    )

    fun existsByCode(code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.identity_type WHERE code = :code AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.identity_type WHERE code = :code AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("code" to code, "excludeId" to excludeId)) > 0
    }
}
