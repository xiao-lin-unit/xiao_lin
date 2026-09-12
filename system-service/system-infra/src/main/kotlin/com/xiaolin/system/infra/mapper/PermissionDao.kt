package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.common.constants.PermissionType
import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.Permission
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.OffsetDateTime

class PermissionDao(jdbc: NamedParameterJdbcTemplate
): AbstractJdbcDao<Permission>(jdbc, Permission::class) {
    override val rowMapper: RowMapper<Permission> = rowMapperOf { rs ->
        Permission(
            id = rs.getLong("id"),
            appId = rs.getLong("app_id"),
            parentId = rs.getLong("parent_id"),
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
            createdBy = rs.getLong("created_by"),
            updatedBy = rs.getLong("updated_by"),
            deletedBy = rs.getLong("deleted_by"),
            deleted = rs.getBoolean("deleted"),
        )
    }

}