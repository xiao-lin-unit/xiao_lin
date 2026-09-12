package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.TenantAggregation
import com.xiaolin.system.domain.repository.TenantRepository
import com.xiaolin.system.infra.converter.TenantConverter
import com.xiaolin.system.infra.mapper.TenantDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class TenantRepositoryImpl(
    private val tenantDao: TenantDao,
) : TenantRepository {

    override fun create(tenant: TenantAggregation): Boolean =
        tenantDao.insert(TenantConverter.aggregation2DO(tenant)) == 1

    override fun update(tenant: TenantAggregation): Boolean =
        tenantDao.update(TenantConverter.aggregation2DO(tenant)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = tenantDao.execute(
        sql = """
            UPDATE sys.tenant
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): TenantAggregation {
        val tenant = tenantDao.findById(id)
        require(tenant != null) { "租户不存在: $id" }
        return TenantConverter.do2Aggregation(tenant)
    }

    override fun getByCode(code: String): TenantAggregation? =
        tenantDao.findByCode(code)?.let { TenantConverter.do2Aggregation(it) }

    override fun existsByCode(code: String, excludeId: Long?): Boolean =
        tenantDao.existsByCode(code, excludeId)

    override fun list(tenantTypeId: Long?, pageable: Pageable): Page<TenantAggregation> {
        val where = if (tenantTypeId == null) "deleted = false" else "deleted = false AND tenant_type_id = :tenantTypeId"
        val params = MapSqlParameterSource().apply {
            if (tenantTypeId != null) addValue("tenantTypeId", tenantTypeId)
        }
        return tenantDao.queryPage(
            sql = "SELECT * FROM sys.tenant WHERE $where ORDER BY id",
            countSql = "SELECT COUNT(*) FROM sys.tenant WHERE $where",
            params = params,
            pageable = pageable,
        ).map { TenantConverter.do2Aggregation(it) }
    }

    override fun listByParentId(parentId: Long?): List<TenantAggregation> =
        tenantDao.findByParentId(parentId).map { TenantConverter.do2Aggregation(it) }

    override fun listByPathPrefix(path: String): List<TenantAggregation> =
        tenantDao.findByPathPrefix(path).map { TenantConverter.do2Aggregation(it) }

    override fun countByTenantTypeId(tenantTypeId: Long): Long =
        tenantDao.countByTenantTypeId(tenantTypeId)
}
