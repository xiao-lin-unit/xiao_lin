package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.common.constants.AppType
import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.App
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class AppDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<App>(jdbc, App::class) {

    override val rowMapper: RowMapper<App> = rowMapperOf { rs ->
        App(
            id = rs.getLong("id"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            type = AppType.valueOf(rs.getString("type") ?: "BUSINESS"),
            entryUrl = rs.getString("entry_url"),
            icon = rs.getString("icon"),
            description = rs.getString("description"),
            status = rs.getInt("status"),
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

    fun findByCode(code: String): App? = queryOne(
        sql = "SELECT * FROM sys.app WHERE code = :code AND deleted = false",
        params = paramsOf("code" to code),
    )

    fun existsByCode(code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.app WHERE code = :code AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.app WHERE code = :code AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("code" to code, "excludeId" to excludeId)) > 0
    }
}
