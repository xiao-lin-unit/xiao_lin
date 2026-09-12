package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.TenantTypeAggregation
import com.xiaolin.system.interfaces.vo.TenantTypeVO

object TenantTypeVOConverter {

    fun toTenantTypeVO(a: TenantTypeAggregation): TenantTypeVO = TenantTypeVO(
        id = a.id,
        code = a.code,
        name = a.name,
        description = a.description,
        orgLevels = a.orgLevels,
        defaultApps = a.defaultApps,
        status = a.status,
        builtin = a.builtin,
        sortNo = a.sortNo,
        createdAt = a.createdAt,
    )
}
