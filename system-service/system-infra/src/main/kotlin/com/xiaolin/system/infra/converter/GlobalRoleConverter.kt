package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.GlobalRoleAggregation
import com.xiaolin.system.infra.persistence.GlobalRole

object GlobalRoleConverter {

    @JvmStatic
    fun aggregation2DO(globalRole: GlobalRoleAggregation): GlobalRole = GlobalRole(
        id = globalRole.id,
        code = globalRole.code,
        name = globalRole.name,
        description = globalRole.description,
        isSuper = globalRole.isSuper,
        builtin = globalRole.builtin,
        status = globalRole.status,
        createdAt = globalRole.createdAt,
        updatedAt = globalRole.updatedAt,
        deletedAt = globalRole.deletedAt,
        createdBy = globalRole.createdBy,
        updatedBy = globalRole.updatedBy,
        deletedBy = globalRole.deletedBy,
        deleted = globalRole.deleted,
    )

    @JvmStatic
    fun do2Aggregation(globalRole: GlobalRole): GlobalRoleAggregation =
        GlobalRoleAggregation.builder(globalRole.id)
            .code(globalRole.code)
            .name(globalRole.name)
            .description(globalRole.description)
            .isSuper(globalRole.isSuper)
            .builtin(globalRole.builtin)
            .status(globalRole.status)
            .createdAt(globalRole.createdAt)
            .updatedAt(globalRole.updatedAt)
            .deletedAt(globalRole.deletedAt)
            .createdBy(globalRole.createdBy)
            .updatedBy(globalRole.updatedBy)
            .deletedBy(globalRole.deletedBy)
            .deleted(globalRole.deleted)
            .build()
}
