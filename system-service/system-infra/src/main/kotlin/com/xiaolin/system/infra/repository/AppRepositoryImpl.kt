package com.xiaolin.system.infra.repository

import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.system.domain.aggregation.AppAggregation
import com.xiaolin.system.domain.repository.AppRepository
import com.xiaolin.system.infra.converter.AppConverter
import com.xiaolin.system.infra.mapper.AppDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class AppRepositoryImpl(
    private val appDao: AppDao,
) : AppRepository {

    override fun create(app: AppAggregation): Boolean =
        appDao.insert(AppConverter.aggregation2DO(app)) == 1

    override fun update(app: AppAggregation): Boolean =
        appDao.update(AppConverter.aggregation2DO(app)) == 1

    override fun deleteById(id: Long, operatorId: Long?): Boolean = appDao.execute(
        sql = """
            UPDATE sys.app
               SET deleted = true, deleted_at = now(), deleted_by = :operatorId
             WHERE id = :id AND deleted = false
        """.trimIndent(),
        params = paramsOf("id" to id, "operatorId" to operatorId),
    ) == 1

    override fun getById(id: Long): AppAggregation {
        val app = appDao.findById(id)
        require(app != null) { "应用不存在: $id" }
        return AppConverter.do2Aggregation(app)
    }

    override fun getByCode(code: String): AppAggregation? =
        appDao.findByCode(code)?.let { AppConverter.do2Aggregation(it) }

    override fun existsByCode(code: String, excludeId: Long?): Boolean =
        appDao.existsByCode(code, excludeId)

    override fun list(pageable: Pageable): Page<AppAggregation> = appDao.queryPage(
        sql = "SELECT * FROM sys.app WHERE deleted = false ORDER BY sort_no, id",
        countSql = "SELECT COUNT(*) FROM sys.app WHERE deleted = false",
        params = MapSqlParameterSource(),
        pageable = pageable,
    ).map { AppConverter.do2Aggregation(it) }
}
