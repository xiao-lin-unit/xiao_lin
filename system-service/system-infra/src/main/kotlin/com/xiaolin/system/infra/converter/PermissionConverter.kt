package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.PermissionAggregation
import com.xiaolin.system.infra.persistence.Permission

object PermissionConverter {

    @JvmStatic
    fun aggregation2DO(permissionAggregation: PermissionAggregation): Permission {
        return Permission(
            id = permissionAggregation.id,
            appId = permissionAggregation.appId,
            parentId = permissionAggregation.parentId,
            code = permissionAggregation.code,
            name = permissionAggregation.name,
            type = permissionAggregation.type,
            apiMethod = permissionAggregation.apiMethod,
            apiPattern = permissionAggregation.apiPattern,
            componentPath = permissionAggregation.componentPath,
            icon = permissionAggregation.icon,
            sortNo = permissionAggregation.sortNo,
            status = permissionAggregation.status,
            builtin = permissionAggregation.builtin,
            remark = permissionAggregation.remark,
            createdAt = permissionAggregation.createdAt,
            updatedAt = permissionAggregation.updatedAt,
            deletedAt = permissionAggregation.deletedAt,
            createdBy = permissionAggregation.createdBy,
            updatedBy = permissionAggregation.updatedBy,
            deletedBy = permissionAggregation.deletedBy,
            deleted = permissionAggregation.deleted
        )
    }

    @JvmStatic
    fun do2Aggregation(permission: Permission): PermissionAggregation {
        return PermissionAggregation(
            id = permission.id,
            appId = permission.appId,
            parentId = permission.parentId,
            code = permission.code,
            name = permission.name,
            type = permission.type,
            apiMethod = permission.apiMethod,
            apiPattern = permission.apiPattern,
            componentPath = permission.componentPath,
            icon = permission.icon,
            sortNo = permission.sortNo,
            status = permission.status,
            builtin = permission.builtin,
            remark = permission.remark,
            createdAt = permission.createdAt,
            updatedAt = permission.updatedAt,
            deletedAt = permission.deletedAt,
            createdBy = permission.createdBy,
            updatedBy = permission.updatedBy,
            deletedBy = permission.deletedBy,
            deleted = permission.deleted
        )
    }


}