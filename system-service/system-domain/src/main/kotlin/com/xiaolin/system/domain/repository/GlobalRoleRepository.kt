package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.GlobalRoleAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GlobalRoleRepository {

    fun create(globalRole: GlobalRoleAggregation): Boolean

    fun update(globalRole: GlobalRoleAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): GlobalRoleAggregation

    fun getByCode(code: String): GlobalRoleAggregation?

    fun existsByCode(code: String, excludeId: Long? = null): Boolean

    fun list(pageable: Pageable): Page<GlobalRoleAggregation>

    /** 超管角色，系统内应唯一 */
    fun findSuper(): GlobalRoleAggregation?
}
