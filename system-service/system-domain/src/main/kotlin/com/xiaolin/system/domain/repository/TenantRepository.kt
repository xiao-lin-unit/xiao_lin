package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.TenantAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TenantRepository {

    fun create(tenant: TenantAggregation): Boolean

    fun update(tenant: TenantAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): TenantAggregation

    fun getByCode(code: String): TenantAggregation?

    fun existsByCode(code: String, excludeId: Long? = null): Boolean

    /** 按租户类型过滤 */
    fun list(tenantTypeId: Long?, pageable: Pageable): Page<TenantAggregation>

    /** 直接子节点（parentId = id） */
    fun listByParentId(parentId: Long?): List<TenantAggregation>

    /** 按 path 前缀取整棵子树，如 /1/12/ 命中 12 及其所有后代 */
    fun listByPathPrefix(path: String): List<TenantAggregation>

    /** 统计某租户类型下已有多少租户，用于删除租户类型前的引用检查 */
    fun countByTenantTypeId(tenantTypeId: Long): Long
}
