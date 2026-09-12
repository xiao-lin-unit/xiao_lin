package com.xiaolin.system.infra.repository

import com.xiaolin.system.domain.aggregation.PermissionAggregation
import com.xiaolin.system.domain.repository.PermissionRepository
import com.xiaolin.system.infra.converter.PermissionConverter
import com.xiaolin.system.infra.mapper.PermissionDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class PermissionRepositoryImpl(
    private val permissionDao: PermissionDao
): PermissionRepository {
    override fun create(permission: PermissionAggregation): Boolean {
        val permissionDO = PermissionConverter.aggregation2DO(permissionAggregation = permission)
        return permissionDao.insert(entity = permissionDO) == 1
    }

    override fun update(permission: PermissionAggregation): Boolean {
        val permissionDO = PermissionConverter.aggregation2DO(permissionAggregation = permission)
        return permissionDao.update(entity = permissionDO) == 1
    }

    override fun deleteById(id: Long): Boolean {
        return permissionDao.deleteById(id = id) == 1
    }

    override fun getById(id: Long): PermissionAggregation {
        val permission = permissionDao.findById(id = id)
        require(permission != null) { "Permission not found by id: $id" }
        return PermissionConverter.do2Aggregation(permission)
    }

    override fun list(): Page<PermissionAggregation> {
        val list = permissionDao.queryPage(
            sql = "SELECT * FROM sys.permission WHERE deleted = false",
            countSql = "SELECT COUNT(*) FROM sys.permission WHERE deleted = false",
            params = MapSqlParameterSource(),
            pageable = PageRequest.of(0, 10)
        )
        return list.map { PermissionConverter.do2Aggregation(it) }
    }
}