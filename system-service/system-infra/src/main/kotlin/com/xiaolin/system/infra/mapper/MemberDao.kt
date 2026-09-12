package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.Member
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class MemberDao(
    jdbc: NamedParameterJdbcTemplate,
) : AbstractJdbcDao<Member>(jdbc, Member::class) {

    override val rowMapper: RowMapper<Member> = rowMapperOf { rs ->
        Member(
            id = rs.getLong("id"),
            tenantId = rs.getLong("tenant_id"),
            userId = rs.getLong("user_id"),
            memberNo = rs.getString("member_no"),
            displayName = rs.getString("display_name"),
            orgId = rs.getLong("org_id"),
            isTenantAdmin = rs.getBoolean("is_tenant_admin"),
            status = rs.getInt("status"),
            joinedAt = rs.getObject("joined_at", OffsetDateTime::class.java),
            profile = rs.getString("profile") ?: "{}",
            invitedBy = rs.getLong("invited_by"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getLong("created_by"),
            updatedBy = rs.getLong("updated_by"),
            deletedBy = rs.getLong("deleted_by"),
            deleted = rs.getBoolean("deleted"),
        )
    }

    fun findByTenantAndUser(tenantId: Long, userId: Long): Member? = queryOne(
        sql = "SELECT * FROM sys.member WHERE tenant_id = :tenantId AND user_id = :userId AND deleted = false",
        params = paramsOf("tenantId" to tenantId, "userId" to userId),
    )

    fun existsByTenantAndUser(tenantId: Long, userId: Long, excludeId: Long? = null): Boolean {
        val sql = if (excludeId == null) {
            "SELECT COUNT(*) FROM sys.member WHERE tenant_id = :tenantId AND user_id = :userId AND deleted = false"
        } else {
            "SELECT COUNT(*) FROM sys.member WHERE tenant_id = :tenantId AND user_id = :userId AND id <> :excludeId AND deleted = false"
        }
        return count(sql, paramsOf("tenantId" to tenantId, "userId" to userId, "excludeId" to excludeId)) > 0
    }

    /** 某用户在所有租户下的成员身份，用于"切换租户" */
    fun findByUserId(userId: Long): List<Member> = query(
        sql = "SELECT * FROM sys.member WHERE user_id = :userId AND deleted = false ORDER BY tenant_id",
        params = paramsOf("userId" to userId),
    )

    fun findByOrgId(tenantId: Long, orgId: Long): List<Member> = query(
        sql = "SELECT * FROM sys.member WHERE tenant_id = :tenantId AND org_id = :orgId AND deleted = false ORDER BY id",
        params = paramsOf("tenantId" to tenantId, "orgId" to orgId),
    )
}
