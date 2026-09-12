package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.RoleAggregation
import com.xiaolin.system.domain.repository.RoleRepository
import com.xiaolin.system.infra.converter.RoleConverter
import com.xiaolin.system.infra.mapper.RoleDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class RoleRepositoryImpl(
    private val roleDao: RoleDao,
) : RoleRepository {

    override fun create(role: RoleAggregation): Boolean =
        roleDao.insert(RoleConverter.aggregation2DO(role)) == 1

    override fun update(role: RoleAggregation): Boolean =
        roleDao.update(RoleConverter.aggregation2DO(role)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = roleDao.execute(
        sql = """
            UPDATE sys.role
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): RoleAggregation {
        val role = roleDao.findById(id)
        require(role != null) { "角色不存在: $id" }
        return RoleConverter.do2Aggregation(role)
    }

    override fun getByTenantAppAndCode(tenantId: Long, appId: Long, code: String): RoleAggregation? =
        roleDao.findByTenantAppAndCode(tenantId, appId, code)?.let { RoleConverter.do2Aggregation(it) }

    override fun existsByTenantAppAndCode(tenantId: Long, appId: Long, code: String, excludeId: Long?): Boolean =
        roleDao.existsByTenantAppAndCode(tenantId, appId, code, excludeId)

    override fun list(tenantId: Long, appId: Long?, pageable: Pageable): Page<RoleAggregation> {
        val where = if (appId == null) {
            "deleted = false AND tenant_id = :tenantId"
        } else {
            "deleted = false AND tenant_id = :tenantId AND app_id = :appId"
        }
        val params = MapSqlParameterSource().apply {
            addValue("tenantId", tenantId)
            if (appId != null) addValue("appId", appId)
        }
        return roleDao.queryPage(
            sql = "SELECT * FROM sys.role WHERE $where ORDER BY sort_no, id",
            countSql = "SELECT COUNT(*) FROM sys.role WHERE $where",
            params = params,
            pageable = pageable,
        ).map { RoleConverter.do2Aggregation(it) }
    }

    override fun listByParentId(tenantId: Long, parentId: Long): List<RoleAggregation> =
        roleDao.findByParentId(tenantId, parentId).map { RoleConverter.do2Aggregation(it) }

    override fun countByParentId(tenantId: Long, parentId: Long): Long =
        roleDao.countByParentId(tenantId, parentId)
}
