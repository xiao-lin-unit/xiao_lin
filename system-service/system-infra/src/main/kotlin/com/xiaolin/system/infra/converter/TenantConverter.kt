package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.TenantAggregation
import com.xiaolin.system.infra.persistence.Tenant

object TenantConverter {

    @JvmStatic
    fun aggregation2DO(tenant: TenantAggregation): Tenant = Tenant(
        id = tenant.id,
        tenantTypeId = tenant.tenantTypeId,
        parentId = tenant.parentId,
        path = tenant.path,
        levelNo = tenant.levelNo,
        code = tenant.code,
        name = tenant.name,
        shortName = tenant.shortName,
        regionCode = tenant.regionCode,
        regionName = tenant.regionName,
        address = tenant.address,
        contactName = tenant.contactName,
        contactPhone = tenant.contactPhone,
        status = tenant.status,
        expireAt = tenant.expireAt,
        config = tenant.config,
        createdAt = tenant.createdAt,
        updatedAt = tenant.updatedAt,
        deletedAt = tenant.deletedAt,
        createdBy = tenant.createdBy,
        updatedBy = tenant.updatedBy,
        deletedBy = tenant.deletedBy,
        deleted = tenant.deleted,
    )

    @JvmStatic
    fun do2Aggregation(tenant: Tenant): TenantAggregation =
        TenantAggregation.builder(tenant.id)
            .tenantTypeId(tenant.tenantTypeId)
            .parentId(tenant.parentId)
            .path(tenant.path)
            .levelNo(tenant.levelNo)
            .code(tenant.code)
            .name(tenant.name)
            .shortName(tenant.shortName)
            .regionCode(tenant.regionCode)
            .regionName(tenant.regionName)
            .address(tenant.address)
            .contactName(tenant.contactName)
            .contactPhone(tenant.contactPhone)
            .status(tenant.status)
            .expireAt(tenant.expireAt)
            .config(tenant.config)
            .createdAt(tenant.createdAt)
            .updatedAt(tenant.updatedAt)
            .deletedAt(tenant.deletedAt)
            .createdBy(tenant.createdBy)
            .updatedBy(tenant.updatedBy)
            .deletedBy(tenant.deletedBy)
            .deleted(tenant.deleted)
            .build()
}
