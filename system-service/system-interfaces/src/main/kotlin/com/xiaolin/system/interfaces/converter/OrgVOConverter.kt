package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.OrgAggregation
import com.xiaolin.system.interfaces.vo.OrgVO

object OrgVOConverter {

    fun toOrgVO(a: OrgAggregation): OrgVO = OrgVO(
        id = a.id,
        tenantId = a.tenantId,
        parentId = a.parentId,
        path = a.path,
        levelNo = a.levelNo,
        code = a.code,
        name = a.name,
        leaderMemberId = a.leaderMemberId,
        sortNo = a.sortNo,
        status = a.status,
    )
}
