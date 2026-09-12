package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.IdentityTypeAggregation
import com.xiaolin.system.infra.persistence.IdentityType

object IdentityTypeConverter {

    @JvmStatic
    fun aggregation2DO(identityType: IdentityTypeAggregation): IdentityType = IdentityType(
        id = identityType.id,
        code = identityType.code,
        name = identityType.name,
        tenantTypeId = identityType.tenantTypeId,
        description = identityType.description,
        builtin = identityType.builtin,
        sortNo = identityType.sortNo,
        createdAt = identityType.createdAt,
        updatedAt = identityType.updatedAt,
        deletedAt = identityType.deletedAt,
        createdBy = identityType.createdBy,
        updatedBy = identityType.updatedBy,
        deletedBy = identityType.deletedBy,
        deleted = identityType.deleted,
    )

    @JvmStatic
    fun do2Aggregation(identityType: IdentityType): IdentityTypeAggregation =
        IdentityTypeAggregation.builder(identityType.id)
            .code(identityType.code)
            .name(identityType.name)
            .tenantTypeId(identityType.tenantTypeId)
            .description(identityType.description)
            .builtin(identityType.builtin)
            .sortNo(identityType.sortNo)
            .createdAt(identityType.createdAt)
            .updatedAt(identityType.updatedAt)
            .deletedAt(identityType.deletedAt)
            .createdBy(identityType.createdBy)
            .updatedBy(identityType.updatedBy)
            .deletedBy(identityType.deletedBy)
            .deleted(identityType.deleted)
            .build()
}
