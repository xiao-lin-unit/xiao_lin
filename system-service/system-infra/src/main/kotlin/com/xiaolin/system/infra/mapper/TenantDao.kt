package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.Tenant
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class TenantDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<Tenant>(jdbc, Tenant::class) {

    override val rowMapper: RowMapper<Tenant> = rowMapperOf { rs ->
        Tenant(
            id = rs.getLong("id"),
            tenantTypeId = rs.getObject("tenant_type_id", Long::class.java),
            parentId = rs.getObject("parent_id", Long::class.java),
            path = rs.getString("path") ?: "/",
            levelNo = rs.getInt("level_no"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            shortName = rs.getString("short_name"),
            regionCode = rs.getString("region_code"),
            regionName = rs.getString("region_name"),
            address = rs.getString("address"),
            contactName = rs.getString("contact_name"),
            contactPhone = rs.getString("contact_phone"),
            status = rs.getInt("status"),
            expireAt = rs.getObject("expire_at", OffsetDateTime::class.java),
            config = rs.getString("config") ?: "{}",
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getObject("created_by", Long::class.java),
            updatedBy = rs.getObject("updated_by", Long::class.java),
            deletedBy = rs.getObject("deleted_by", Long::class.java),
            deleted = rs.getBoolean("deleted"),
        )
    }

    fun findByCode(code: String): Tenant? = queryOne(
        sql = "SELECT * FROM sys.tenant WHERE code = :code AND deleted = false",
        params = paramsOf("code" to code),
    )

    fun existsByCode(code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.tenant WHERE code = :code AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.tenant WHERE code = :code AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("code" to code, "excludeId" to excludeId)) > 0
    }

    /**
     * parentId 为 null 时取所有根租户。
     * 注意：PG 下 `parent_id = NULL` 永远为 false，必须走 IS NULL 分支。
     */
    fun findByParentId(parentId: Long?): List<Tenant> = query(
        sql = if (parentId == null) {
            "SELECT * FROM sys.tenant WHERE parent_id IS NULL AND deleted = false ORDER BY sort_no, id"
        } else {
            "SELECT * FROM sys.tenant WHERE parent_id = :parentId AND deleted = false ORDER BY sort_no, id"
        },
        params = paramsOf("parentId" to parentId),
    )

    /**
     * 按 path 前缀取子树。path 形如 /1/12/，用 LIKE 'prefix%' 命中自身与全部后代。
     * prefix 来自库内数据，不含用户输入，但仍走绑定参数以防万一。
     */
    fun findByPathPrefix(path: String): List<Tenant> = query(
        sql = "SELECT * FROM sys.tenant WHERE path LIKE :prefix AND deleted = false ORDER BY path",
        params = paramsOf("prefix" to "$path%"),
    )

    fun countByTenantTypeId(tenantTypeId: Long): Long = count(
        sql = "SELECT COUNT(*) FROM sys.tenant WHERE tenant_type_id = :tenantTypeId AND deleted = false",
        params = paramsOf("tenantTypeId" to tenantTypeId),
    )
}
