package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.Org
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class OrgDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<Org>(jdbc, Org::class) {

    override val rowMapper: RowMapper<Org> = rowMapperOf { rs ->
        Org(
            id = rs.getLong("id"),
            tenantId = rs.getLong("tenant_id"),
            parentId = rs.getLong("parent_id"),
            path = rs.getString("path") ?: "",
            levelNo = rs.getInt("level_no"),
            code = rs.getString("code"),
            name = rs.getString("name"),
            leaderMemberId = rs.getLong("leader_member_id"),
            sortNo = rs.getInt("sort_no"),
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

    fun findByTenantAndCode(tenantId: Long, code: String): Org? = queryOne(
        sql = "SELECT * FROM sys.org WHERE tenant_id = :tenantId AND code = :code AND deleted = false",
        params = paramsOf("tenantId" to tenantId, "code" to code),
    )

    fun existsByTenantAndCode(tenantId: Long, code: String, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.org WHERE tenant_id = :tenantId AND code = :code AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.org WHERE tenant_id = :tenantId AND code = :code AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("tenantId" to tenantId, "code" to code, "excludeId" to excludeId)) > 0
    }

    fun findByParentId(tenantId: Long, parentId: Long?): List<Org> = query(
        sql = if (parentId == null) {
            "SELECT * FROM sys.org WHERE tenant_id = :tenantId AND parent_id IS NULL AND deleted = false ORDER BY sort_no, id"
        } else {
            "SELECT * FROM sys.org WHERE tenant_id = :tenantId AND parent_id = :parentId AND deleted = false ORDER BY sort_no, id"
        },
        params = paramsOf("tenantId" to tenantId, "parentId" to parentId),
    )

    fun countByParentId(tenantId: Long, parentId: Long): Long = count(
        sql = "SELECT COUNT(*) FROM sys.org WHERE tenant_id = :tenantId AND parent_id = :parentId AND deleted = false",
        params = paramsOf("tenantId" to tenantId, "parentId" to parentId),
    )

    fun findByPathPrefix(tenantId: Long, path: String): List<Org> = query(
        sql = "SELECT * FROM sys.org WHERE tenant_id = :tenantId AND path LIKE :prefix AND deleted = false ORDER BY path",
        params = paramsOf("tenantId" to tenantId, "prefix" to "$path%"),
    )
}
