package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.MemberAggregation
import com.xiaolin.system.interfaces.vo.MemberVO

object MemberVOConverter {

    /** 不向外暴露 profile —— 其中含摊位号、经营品类、资质等敏感信息 */
    fun toMemberVO(a: MemberAggregation): MemberVO = MemberVO(
        id = a.id,
        tenantId = a.tenantId,
        userId = a.userId,
        memberNo = a.memberNo,
        displayName = a.displayName,
        orgId = a.orgId,
        isTenantAdmin = a.isTenantAdmin,
        status = a.status,
        joinedAt = a.joinedAt,
        invitedBy = a.invitedBy,
    )
}
