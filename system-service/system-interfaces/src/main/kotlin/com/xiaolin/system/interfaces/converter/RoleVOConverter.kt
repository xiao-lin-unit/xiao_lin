package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.RoleAggregation
import com.xiaolin.system.interfaces.vo.RoleVO

object RoleVOConverter {

    fun toRoleVO(a: RoleAggregation): RoleVO = RoleVO(
        id = a.id,
        tenantId = a.tenantId,
        appId = a.appId,
        parentId = a.parentId,
        code = a.code,
        name = a.name,
        description = a.description,
        isTenantAdmin = a.isTenantAdmin,
        builtin = a.builtin,
        status = a.status,
        sortNo = a.sortNo,
    )
}
