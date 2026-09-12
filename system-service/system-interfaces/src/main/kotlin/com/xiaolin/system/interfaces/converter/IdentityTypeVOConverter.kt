package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.IdentityTypeAggregation
import com.xiaolin.system.interfaces.vo.IdentityTypeVO

object IdentityTypeVOConverter {

    fun toIdentityTypeVO(a: IdentityTypeAggregation): IdentityTypeVO = IdentityTypeVO(
        id = a.id,
        code = a.code,
        name = a.name,
        tenantTypeId = a.tenantTypeId,
        description = a.description,
        builtin = a.builtin,
        sortNo = a.sortNo,
    )
}
