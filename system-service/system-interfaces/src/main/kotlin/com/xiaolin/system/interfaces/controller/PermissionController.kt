package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.PermissionCreateCommand
import com.xiaolin.system.application.service.PermissionService
import com.xiaolin.system.interfaces.converter.PermissionVOConverter
import com.xiaolin.system.interfaces.vo.PermissionVO
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/permission")
class PermissionController(
    private val permissionService: PermissionService
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid permission: PermissionCreateCommand): ApiResult<PermissionVO> {
        val aggregation = permissionService.create(permission)
        return ApiResult.success(PermissionVOConverter.toPermissionVO(aggregation))
    }

    @PostMapping("/getById")
    fun getById(id: Long): ApiResult<PermissionVO> {
        val aggregation = permissionService.getById(id)
        return ApiResult.success(PermissionVOConverter.toPermissionVO(aggregation))
    }
    @PostMapping("/list")
    fun list(): ApiResult<Page<PermissionVO>> {
        val aggregations = permissionService.list()
        return ApiResult.success(aggregations.map { PermissionVOConverter.toPermissionVO(it) })
    }
}