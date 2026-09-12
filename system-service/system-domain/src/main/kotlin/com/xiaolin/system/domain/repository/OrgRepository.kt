package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.OrgAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrgRepository {

    fun create(org: OrgAggregation): Boolean

    fun update(org: OrgAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): OrgAggregation

    /** 租户内按 code 查，uk_org_tenant_code 保证 (tenant_id, code) 唯一 */
    fun getByTenantAndCode(tenantId: Long, code: String): OrgAggregation?

    fun existsByTenantAndCode(tenantId: Long, code: String, excludeId: Long? = null): Boolean

    fun list(tenantId: Long, pageable: Pageable): Page<OrgAggregation>

    /** 直接子节点 */
    fun listByParentId(tenantId: Long, parentId: Long?): List<OrgAggregation>

    /** 按 path 前缀取子树，支撑 ORG_TREE 数据范围 */
    fun listByPathPrefix(tenantId: Long, path: String): List<OrgAggregation>

    /** 删除前校验用：直属子节点数量 */
    fun countByParentId(tenantId: Long, parentId: Long): Long
}
