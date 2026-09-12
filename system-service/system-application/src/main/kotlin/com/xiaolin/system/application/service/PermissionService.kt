package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.PermissionCreateCommand
import com.xiaolin.system.domain.aggregation.PermissionAggregation
import com.xiaolin.system.domain.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service


@Service
class PermissionService(
    private val permissionRepository: PermissionRepository,
    private val idGenerator: IdGenerator,

    ) {

    fun create(permission: PermissionCreateCommand): PermissionAggregation {
        val builder = PermissionAggregation.create(idGenerator.nextId())
        val permissionAggregation = builder.name(permission.name)
            .appId(permission.appId ?: 0L)
            .parentId(permission.parentId)
            .code(permission.code)
            .type(permission.type)
            .apiMethod(permission.apiMethod)
            .apiPattern(permission.apiPattern)
            .componentPath(permission.componentPath)
            .icon(permission.icon)
            .sortNo(permission.sortNo)
            .builtin(permission.builtin ?: true)
            .remark(permission.remark)
            .build()

        permissionAggregation.enable()
        permissionAggregation.verify()

        permissionRepository.create(permissionAggregation);

        return permissionAggregation
    }

    fun getById(id: Long): PermissionAggregation {
        return permissionRepository.getById(id)
    }

    fun list(): Page<PermissionAggregation> {
        return permissionRepository.list()
    }


}