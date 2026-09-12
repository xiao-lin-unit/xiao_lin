package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.AppAggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AppRepository {

    fun create(app: AppAggregation): Boolean

    fun update(app: AppAggregation): Boolean

    /** 逻辑删除：置 deleted = true 并记录操作人，数据不物理移除 */
    fun deleteById(id: Long, operatorId: Long? = null): Boolean

    fun getById(id: Long): AppAggregation

    fun getByCode(code: String): AppAggregation?

    fun existsByCode(code: String, excludeId: Long? = null): Boolean

    fun list(pageable: Pageable): Page<AppAggregation>
}
