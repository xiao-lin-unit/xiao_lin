package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.GlobalRoleAggregation
import com.xiaolin.system.interfaces.vo.GlobalRoleVO

object GlobalRoleVOConverter {

    fun toGlobalRoleVO(a: GlobalRoleAggregation): GlobalRoleVO = GlobalRoleVO(
        id = a.id,
        code = a.code,
        name = a.name,
        description = a.description,
        isSuper = a.isSuper,
        builtin = a.builtin,
        status = a.status,
    )
}
