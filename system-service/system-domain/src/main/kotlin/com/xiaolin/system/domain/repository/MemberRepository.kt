package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.MemberAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepository {

    fun create(member: MemberAggregation): Boolean

    fun update(member: MemberAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): MemberAggregation

    /** uk_member_tenant_user 保证同一租户下 user 唯一 */
    fun getByTenantAndUser(tenantId: Long, userId: Long): MemberAggregation?

    fun existsByTenantAndUser(tenantId: Long, userId: Long, excludeId: Long? = null): Boolean

    fun list(tenantId: Long, pageable: Pageable): Page<MemberAggregation>

    /** 某用户在所有租户下的成员身份，支撑"切换租户" */
    fun listByUserId(userId: Long): List<MemberAggregation>

    fun listByOrgId(tenantId: Long, orgId: Long): List<MemberAggregation>
}
