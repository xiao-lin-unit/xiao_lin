package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.common.constants.PermissionType
import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.Permission
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class PermissionDao(jdbc: NamedParameterJdbcTemplate
): AbstractJdbcDao<Permission>(jdbc, Permission::class) {
    override val rowMapper: RowMapper<Permission> = rowMapperOf { rs ->
        Permission(
            id = rs.getLong("id"),
            appId = rs.getObject("app_id", Long::class.java),
            parentId = rs.getObject("parent_id", Long::class.java),
            code = rs.getString("code"),
            name = rs.getString("name"),
            type = PermissionType.valueOf(rs.getString("type")),
            apiMethod = rs.getString("api_method"),
            apiPattern = rs.getString("api_pattern"),
            componentPath = rs.getString("component_path"),
            icon = rs.getString("icon"),
            sortNo = rs.getInt("sort_no"),
            status = rs.getInt("status"),
            builtin = rs.getBoolean("builtin"),
            remark = rs.getString("remark"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getObject("created_by", Long::class.java),
            updatedBy = rs.getObject("updated_by", Long::class.java),
            deletedBy = rs.getObject("deleted_by", Long::class.java),
            deleted = rs.getBoolean("deleted"),
        )
    }

}