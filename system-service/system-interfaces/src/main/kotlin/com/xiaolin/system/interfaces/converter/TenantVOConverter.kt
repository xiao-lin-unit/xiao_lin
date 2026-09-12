package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.TenantAggregation
import com.xiaolin.system.interfaces.vo.TenantVO

object TenantVOConverter {

    /** 不向外暴露 config —— 其中含租户个性化开关与内部参数 */
    fun toTenantVO(a: TenantAggregation): TenantVO = TenantVO(
        id = a.id,
        tenantTypeId = a.tenantTypeId,
        parentId = a.parentId,
        path = a.path,
        levelNo = a.levelNo,
        code = a.code,
        name = a.name,
        shortName = a.shortName,
        regionCode = a.regionCode,
        regionName = a.regionName,
        address = a.address,
        contactName = a.contactName,
        contactPhone = a.contactPhone,
        status = a.status,
        expireAt = a.expireAt,
        createdAt = a.createdAt,
    )
}
