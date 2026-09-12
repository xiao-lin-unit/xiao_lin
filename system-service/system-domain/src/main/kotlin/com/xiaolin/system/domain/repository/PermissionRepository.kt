package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.PermissionAggregation
import org.springframework.data.domain.Page

interface PermissionRepository {

    fun create(permission: PermissionAggregation): Boolean

    fun update(permission: PermissionAggregation): Boolean

    fun deleteById(id: Long): Boolean

    fun getById(id: Long): PermissionAggregation

    fun list(): Page<PermissionAggregation>

}