package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.GlobalRoleAggregation
import com.xiaolin.system.domain.repository.GlobalRoleRepository
import com.xiaolin.system.infra.converter.GlobalRoleConverter
import com.xiaolin.system.infra.mapper.GlobalRoleDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class GlobalRoleRepositoryImpl(
    private val globalRoleDao: GlobalRoleDao,
) : GlobalRoleRepository {

    override fun create(globalRole: GlobalRoleAggregation): Boolean =
        globalRoleDao.insert(GlobalRoleConverter.aggregation2DO(globalRole)) == 1

    override fun update(globalRole: GlobalRoleAggregation): Boolean =
        globalRoleDao.update(GlobalRoleConverter.aggregation2DO(globalRole)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = globalRoleDao.execute(
        sql = """
            UPDATE sys.global_role
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): GlobalRoleAggregation {
        val globalRole = globalRoleDao.findById(id)
        require(globalRole != null) { "全局角色不存在: $id" }
        return GlobalRoleConverter.do2Aggregation(globalRole)
    }

    override fun getByCode(code: String): GlobalRoleAggregation? =
        globalRoleDao.findByCode(code)?.let { GlobalRoleConverter.do2Aggregation(it) }

    override fun existsByCode(code: String, excludeId: Long?): Boolean =
        globalRoleDao.existsByCode(code, excludeId)

    override fun list(pageable: Pageable): Page<GlobalRoleAggregation> = globalRoleDao.queryPage(
        sql = "SELECT * FROM sys.global_role WHERE deleted = false ORDER BY id",
        countSql = "SELECT COUNT(*) FROM sys.global_role WHERE deleted = false",
        params = MapSqlParameterSource(),
        pageable = pageable,
    ).map { GlobalRoleConverter.do2Aggregation(it) }

    override fun findSuper(): GlobalRoleAggregation? =
        globalRoleDao.findSuper()?.let { GlobalRoleConverter.do2Aggregation(it) }
}
