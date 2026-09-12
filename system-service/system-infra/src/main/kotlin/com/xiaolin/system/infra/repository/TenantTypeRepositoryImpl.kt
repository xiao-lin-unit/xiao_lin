package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.TenantTypeAggregation
import com.xiaolin.system.domain.repository.TenantTypeRepository
import com.xiaolin.system.infra.converter.TenantTypeConverter
import com.xiaolin.system.infra.mapper.TenantTypeDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class TenantTypeRepositoryImpl(
    private val tenantTypeDao: TenantTypeDao,
) : TenantTypeRepository {

    override fun create(tenantType: TenantTypeAggregation): Boolean =
        tenantTypeDao.insert(TenantTypeConverter.aggregation2DO(tenantType)) == 1

    override fun update(tenantType: TenantTypeAggregation): Boolean =
        tenantTypeDao.update(TenantTypeConverter.aggregation2DO(tenantType)) == 1

    /**
     * 逻辑删除。
     * sys.tenant_type 的 deleted / deleted_at / deleted_by 均为可空列，
     * 这里显式赋值以便审计；物理删除会把历史租户的类型归属一起抹掉。
     */
    override fun deleteById(id: Long, operatorId: Long?): Boolean = tenantTypeDao.execute(
        sql = """
            UPDATE sys.tenant_type
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): TenantTypeAggregation {
        val tenantType = tenantTypeDao.findById(id)
        require(tenantType != null) { "租户类型不存在: $id" }
        return TenantTypeConverter.do2Aggregation(tenantType)
    }

    override fun getByCode(code: String): TenantTypeAggregation? =
        tenantTypeDao.findByCode(code)?.let { TenantTypeConverter.do2Aggregation(it) }

    override fun existsByCode(code: String, excludeId: Long?): Boolean =
        tenantTypeDao.existsByCode(code, excludeId)

    override fun list(pageable: Pageable): Page<TenantTypeAggregation> = tenantTypeDao.queryPage(
        sql = "SELECT * FROM sys.tenant_type WHERE deleted = false ORDER BY sort_no, id",
        countSql = "SELECT COUNT(*) FROM sys.tenant_type WHERE deleted = false",
        params = MapSqlParameterSource(),
        pageable = pageable,
    ).map { TenantTypeConverter.do2Aggregation(it) }
}
