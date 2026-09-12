package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.OrgAggregation
import com.xiaolin.system.domain.repository.OrgRepository
import com.xiaolin.system.infra.converter.OrgConverter
import com.xiaolin.system.infra.mapper.OrgDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class OrgRepositoryImpl(
    private val orgDao: OrgDao,
) : OrgRepository {

    override fun create(org: OrgAggregation): Boolean =
        orgDao.insert(OrgConverter.aggregation2DO(org)) == 1

    override fun update(org: OrgAggregation): Boolean =
        orgDao.update(OrgConverter.aggregation2DO(org)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = orgDao.execute(
        sql = """
            UPDATE sys.org
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): OrgAggregation {
        val org = orgDao.findById(id)
        require(org != null) { "组织不存在: $id" }
        return OrgConverter.do2Aggregation(org)
    }

    override fun getByTenantAndCode(tenantId: Long, code: String): OrgAggregation? =
        orgDao.findByTenantAndCode(tenantId, code)?.let { OrgConverter.do2Aggregation(it) }

    override fun existsByTenantAndCode(tenantId: Long, code: String, excludeId: Long?): Boolean =
        orgDao.existsByTenantAndCode(tenantId, code, excludeId)

    override fun list(tenantId: Long, pageable: Pageable): Page<OrgAggregation> = orgDao.queryPage(
        sql = "SELECT * FROM sys.org WHERE tenant_id = :tenantId AND deleted = false ORDER BY sort_no, id",
        countSql = "SELECT COUNT(*) FROM sys.org WHERE tenant_id = :tenantId AND deleted = false",
        params = MapSqlParameterSource().addValue("tenantId", tenantId),
        pageable = pageable,
    ).map { OrgConverter.do2Aggregation(it) }

    override fun listByParentId(tenantId: Long, parentId: Long?): List<OrgAggregation> =
        orgDao.findByParentId(tenantId, parentId).map { OrgConverter.do2Aggregation(it) }

    override fun listByPathPrefix(tenantId: Long, path: String): List<OrgAggregation> =
        orgDao.findByPathPrefix(tenantId, path).map { OrgConverter.do2Aggregation(it) }

    override fun countByParentId(tenantId: Long, parentId: Long): Long =
        orgDao.countByParentId(tenantId, parentId)
}
