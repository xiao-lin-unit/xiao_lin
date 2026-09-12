package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.IdentityTypeAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IdentityTypeRepository {

    fun create(identityType: IdentityTypeAggregation): Boolean

    fun update(identityType: IdentityTypeAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): IdentityTypeAggregation

    fun getByCode(code: String): IdentityTypeAggregation?

    fun existsByCode(code: String, excludeId: Long? = null): Boolean

    /** 通用身份（tenant_type_id IS NULL）+ 该租户类型专属身份 */
    fun list(tenantTypeId: Long?, pageable: Pageable): Page<IdentityTypeAggregation>
}
