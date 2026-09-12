package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.AppAggregation
import com.xiaolin.system.interfaces.vo.AppVO

object AppVOConverter {

    fun toAppVO(a: AppAggregation): AppVO = AppVO(
        id = a.id,
        code = a.code,
        name = a.name,
        type = a.type,
        entryUrl = a.entryUrl,
        icon = a.icon,
        description = a.description,
        status = a.status,
        builtin = a.builtin,
        sortNo = a.sortNo,
        createdAt = a.createdAt,
    )
}
