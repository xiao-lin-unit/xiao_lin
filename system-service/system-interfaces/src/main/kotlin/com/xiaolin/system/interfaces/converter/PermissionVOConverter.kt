package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.PermissionAggregation
import com.xiaolin.system.interfaces.vo.PermissionVO

object PermissionVOConverter {


    fun toPermissionVO(permissionAggregation: PermissionAggregation): PermissionVO {
        return PermissionVO(
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
            remark = permissionAggregation.remark
        )
    }
}