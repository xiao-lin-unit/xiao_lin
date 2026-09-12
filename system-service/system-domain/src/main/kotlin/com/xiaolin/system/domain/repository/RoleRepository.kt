package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.RoleAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RoleRepository {

    fun create(role: RoleAggregation): Boolean

    fun update(role: RoleAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): RoleAggregation

    /** uk_role_tenant_app_code 保证 (tenant_id, app_id, code) 唯一 */
    fun getByTenantAppAndCode(tenantId: Long, appId: Long, code: String): RoleAggregation?

    fun existsByTenantAppAndCode(tenantId: Long, appId: Long, code: String, excludeId: Long? = null): Boolean

    /** 租户 + 应用下的角色列表 */
    fun list(tenantId: Long, appId: Long?, pageable: Pageable): Page<RoleAggregation>

    /** 直接子角色，用于展开角色继承树 */
    fun listByParentId(tenantId: Long, parentId: Long): List<RoleAggregation>

    fun countByParentId(tenantId: Long, parentId: Long): Long
}
