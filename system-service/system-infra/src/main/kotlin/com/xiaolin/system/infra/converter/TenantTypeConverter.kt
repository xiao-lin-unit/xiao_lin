package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.TenantTypeAggregation
import com.xiaolin.system.infra.persistence.TenantType

object TenantTypeConverter {

    @JvmStatic
    fun aggregation2DO(tenantType: TenantTypeAggregation): TenantType = TenantType(
        id = tenantType.id,
        code = tenantType.code,
        name = tenantType.name,
        description = tenantType.description,
        orgLevels = tenantType.orgLevels,
        defaultApps = tenantType.defaultApps,
        status = tenantType.status,
        builtin = tenantType.builtin,
        sortNo = tenantType.sortNo,
        createdAt = tenantType.createdAt,
        updatedAt = tenantType.updatedAt,
        deletedAt = tenantType.deletedAt,
        createdBy = tenantType.createdBy,
        updatedBy = tenantType.updatedBy,
        deletedBy = tenantType.deletedBy,
        deleted = tenantType.deleted,
    )

    @JvmStatic
    fun do2Aggregation(tenantType: TenantType): TenantTypeAggregation =
        TenantTypeAggregation.builder(tenantType.id)
            .code(tenantType.code)
            .name(tenantType.name)
            .description(tenantType.description)
            .orgLevels(tenantType.orgLevels)
            .defaultApps(tenantType.defaultApps)
            .status(tenantType.status)
            .builtin(tenantType.builtin)
            .sortNo(tenantType.sortNo)
            .createdAt(tenantType.createdAt)
            .updatedAt(tenantType.updatedAt)
            .deletedAt(tenantType.deletedAt)
            .createdBy(tenantType.createdBy)
            .updatedBy(tenantType.updatedBy)
            .deletedBy(tenantType.deletedBy)
            .deleted(tenantType.deleted)
            .build()
}
