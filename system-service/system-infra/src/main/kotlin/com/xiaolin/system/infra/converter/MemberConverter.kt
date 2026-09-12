package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.MemberAggregation
import com.xiaolin.system.infra.persistence.Member

object MemberConverter {

    @JvmStatic
    fun aggregation2DO(member: MemberAggregation): Member = Member(
        id = member.id,
        tenantId = member.tenantId,
        userId = member.userId,
        memberNo = member.memberNo,
        displayName = member.displayName,
        orgId = member.orgId,
        isTenantAdmin = member.isTenantAdmin,
        status = member.status,
        joinedAt = member.joinedAt,
        profile = member.profile,
        invitedBy = member.invitedBy,
        createdAt = member.createdAt,
        updatedAt = member.updatedAt,
        deletedAt = member.deletedAt,
        createdBy = member.createdBy,
        updatedBy = member.updatedBy,
        deletedBy = member.deletedBy,
        deleted = member.deleted,
    )

    @JvmStatic
    fun do2Aggregation(member: Member): MemberAggregation =
        MemberAggregation.builder(member.id)
            .tenantId(member.tenantId)
            .userId(member.userId)
            .memberNo(member.memberNo)
            .displayName(member.displayName)
            .orgId(member.orgId)
            .isTenantAdmin(member.isTenantAdmin)
            .status(member.status)
            .joinedAt(member.joinedAt)
            .profile(member.profile)
            .invitedBy(member.invitedBy)
            .createdAt(member.createdAt)
            .updatedAt(member.updatedAt)
            .deletedAt(member.deletedAt)
            .createdBy(member.createdBy)
            .updatedBy(member.updatedBy)
            .deletedBy(member.deletedBy)
            .deleted(member.deleted)
            .build()
}
