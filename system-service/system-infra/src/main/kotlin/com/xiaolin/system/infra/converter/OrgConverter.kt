package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.OrgAggregation
import com.xiaolin.system.infra.persistence.Org

object OrgConverter {

    @JvmStatic
    fun aggregation2DO(org: OrgAggregation): Org = Org(
        id = org.id,
        tenantId = org.tenantId,
        parentId = org.parentId,
        path = org.path,
        levelNo = org.levelNo,
        code = org.code,
        name = org.name,
        leaderMemberId = org.leaderMemberId,
        sortNo = org.sortNo,
        status = org.status,
        createdAt = org.createdAt,
        updatedAt = org.updatedAt,
        deletedAt = org.deletedAt,
        createdBy = org.createdBy,
        updatedBy = org.updatedBy,
        deletedBy = org.deletedBy,
        deleted = org.deleted,
    )

    @JvmStatic
    fun do2Aggregation(org: Org): OrgAggregation =
        OrgAggregation.builder(org.id)
            .tenantId(org.tenantId)
            .parentId(org.parentId)
            .path(org.path)
            .levelNo(org.levelNo)
            .code(org.code)
            .name(org.name)
            .leaderMemberId(org.leaderMemberId)
            .sortNo(org.sortNo)
            .status(org.status)
            .createdAt(org.createdAt)
            .updatedAt(org.updatedAt)
            .deletedAt(org.deletedAt)
            .createdBy(org.createdBy)
            .updatedBy(org.updatedBy)
            .deletedBy(org.deletedBy)
            .deleted(org.deleted)
            .build()
}
