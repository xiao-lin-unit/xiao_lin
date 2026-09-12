package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.TenantTypeAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TenantTypeRepository {

    fun create(tenantType: TenantTypeAggregation): Boolean

    fun update(tenantType: TenantTypeAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): TenantTypeAggregation

    fun getByCode(code: String): TenantTypeAggregation?

    fun existsByCode(code: String, excludeId: Long? = null): Boolean

    fun list(pageable: Pageable): Page<TenantTypeAggregation>
}
