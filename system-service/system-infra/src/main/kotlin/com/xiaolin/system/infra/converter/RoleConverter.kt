package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.RoleAggregation
import com.xiaolin.system.infra.persistence.Role

object RoleConverter {

    @JvmStatic
    fun aggregation2DO(role: RoleAggregation): Role = Role(
        id = role.id,
        tenantId = role.tenantId,
        appId = role.appId,
        parentId = role.parentId,
        code = role.code,
        name = role.name,
        description = role.description,
        isTenantAdmin = role.isTenantAdmin,
        builtin = role.builtin,
        status = role.status,
        sortNo = role.sortNo,
        createdAt = role.createdAt,
        updatedAt = role.updatedAt,
        deletedAt = role.deletedAt,
        createdBy = role.createdBy,
        updatedBy = role.updatedBy,
        deletedBy = role.deletedBy,
        deleted = role.deleted,
    )

    @JvmStatic
    fun do2Aggregation(role: Role): RoleAggregation =
        RoleAggregation.builder(role.id)
            .tenantId(role.tenantId)
            .appId(role.appId)
            .parentId(role.parentId)
            .code(role.code)
            .name(role.name)
            .description(role.description)
            .isTenantAdmin(role.isTenantAdmin)
            .builtin(role.builtin)
            .status(role.status)
            .sortNo(role.sortNo)
            .createdAt(role.createdAt)
            .updatedAt(role.updatedAt)
            .deletedAt(role.deletedAt)
            .createdBy(role.createdBy)
            .updatedBy(role.updatedBy)
            .deletedBy(role.deletedBy)
            .deleted(role.deleted)
            .build()
}
