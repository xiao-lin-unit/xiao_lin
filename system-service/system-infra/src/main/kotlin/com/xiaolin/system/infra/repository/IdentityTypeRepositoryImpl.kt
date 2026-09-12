package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.IdentityTypeAggregation
import com.xiaolin.system.domain.repository.IdentityTypeRepository
import com.xiaolin.system.infra.converter.IdentityTypeConverter
import com.xiaolin.system.infra.mapper.IdentityTypeDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class IdentityTypeRepositoryImpl(
    private val identityTypeDao: IdentityTypeDao,
) : IdentityTypeRepository {

    override fun create(identityType: IdentityTypeAggregation): Boolean =
        identityTypeDao.insert(IdentityTypeConverter.aggregation2DO(identityType)) == 1

    override fun update(identityType: IdentityTypeAggregation): Boolean =
        identityTypeDao.update(IdentityTypeConverter.aggregation2DO(identityType)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = identityTypeDao.execute(
        sql = """
            UPDATE sys.identity_type
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): IdentityTypeAggregation {
        val identityType = identityTypeDao.findById(id)
        require(identityType != null) { "身份类型不存在: $id" }
        return IdentityTypeConverter.do2Aggregation(identityType)
    }

    override fun getByCode(code: String): IdentityTypeAggregation? =
        identityTypeDao.findByCode(code)?.let { IdentityTypeConverter.do2Aggregation(it) }

    override fun existsByCode(code: String, excludeId: Long?): Boolean =
        identityTypeDao.existsByCode(code, excludeId)

    /**
     * tenantTypeId 为空 -> 仅通用身份；有值 -> 通用身份 + 该租户类型专属身份。
     * PG 下 `tenant_type_id = NULL` 恒为 false，通用身份必须走 IS NULL 分支。
     */
    override fun list(tenantTypeId: Long?, pageable: Pageable): Page<IdentityTypeAggregation> {
        val where = if (tenantTypeId == null) {
            "deleted = false AND tenant_type_id IS NULL"
        } else {
            "deleted = false AND (tenant_type_id IS NULL OR tenant_type_id = :tenantTypeId)"
        }
        val params = MapSqlParameterSource().apply {
            if (tenantTypeId != null) addValue("tenantTypeId", tenantTypeId)
        }
        return identityTypeDao.queryPage(
            sql = "SELECT * FROM sys.identity_type WHERE $where ORDER BY sort_no, id",
            countSql = "SELECT COUNT(*) FROM sys.identity_type WHERE $where",
            params = params,
            pageable = pageable,
        ).map { IdentityTypeConverter.do2Aggregation(it) }
    }
}
