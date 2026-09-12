package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.MemberAggregation
import com.xiaolin.system.domain.repository.MemberRepository
import com.xiaolin.system.infra.converter.MemberConverter
import com.xiaolin.system.infra.mapper.MemberDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(
    private val memberDao: MemberDao,
) : MemberRepository {

    override fun create(member: MemberAggregation): Boolean =
        memberDao.insert(MemberConverter.aggregation2DO(member)) == 1

    override fun update(member: MemberAggregation): Boolean =
        memberDao.update(MemberConverter.aggregation2DO(member)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = memberDao.execute(
        sql = """
            UPDATE sys.member
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): MemberAggregation {
        val member = memberDao.findById(id)
        require(member != null) { "成员不存在: $id" }
        return MemberConverter.do2Aggregation(member)
    }

    override fun getByTenantAndUser(tenantId: Long, userId: Long): MemberAggregation? =
        memberDao.findByTenantAndUser(tenantId, userId)?.let { MemberConverter.do2Aggregation(it) }

    override fun existsByTenantAndUser(tenantId: Long, userId: Long, excludeId: Long?): Boolean =
        memberDao.existsByTenantAndUser(tenantId, userId, excludeId)

    override fun list(tenantId: Long, pageable: Pageable): Page<MemberAggregation> = memberDao.queryPage(
        sql = "SELECT * FROM sys.member WHERE tenant_id = :tenantId AND deleted = false ORDER BY id",
        countSql = "SELECT COUNT(*) FROM sys.member WHERE tenant_id = :tenantId AND deleted = false",
        params = MapSqlParameterSource().addValue("tenantId", tenantId),
        pageable = pageable,
    ).map { MemberConverter.do2Aggregation(it) }

    override fun listByUserId(userId: Long): List<MemberAggregation> =
        memberDao.findByUserId(userId).map { MemberConverter.do2Aggregation(it) }

    override fun listByOrgId(tenantId: Long, orgId: Long): List<MemberAggregation> =
        memberDao.findByOrgId(tenantId, orgId).map { MemberConverter.do2Aggregation(it) }
}
