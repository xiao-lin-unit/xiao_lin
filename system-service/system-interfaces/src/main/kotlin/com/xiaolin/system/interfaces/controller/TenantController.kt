package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.TenantCreateCommand
import com.xiaolin.system.application.service.TenantService
import com.xiaolin.system.interfaces.converter.TenantVOConverter
import com.xiaolin.system.interfaces.vo.TenantVO
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tenant")
class TenantController(
    private val tenantService: TenantService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: TenantCreateCommand): ApiResult<TenantVO> =
        ApiResult.success(TenantVOConverter.toTenantVO(tenantService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: TenantCreateCommand,
    ): ApiResult<TenantVO> =
        ApiResult.success(TenantVOConverter.toTenantVO(tenantService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        tenantService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<TenantVO> =
        ApiResult.success(TenantVOConverter.toTenantVO(tenantService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam(required = false) tenantTypeId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<TenantVO>> =
        ApiResult.success(
            tenantService.list(tenantTypeId, PageRequest.of(page, size)).map { TenantVOConverter.toTenantVO(it) }
        )

    /** 审核通过 */
    @PostMapping("/{id}/approve")
    fun approve(@PathVariable("id") id: Long): ApiResult<TenantVO> =
        ApiResult.success(TenantVOConverter.toTenantVO(tenantService.approve(id)))

    @PostMapping("/{id}/freeze")
    fun freeze(@PathVariable("id") id: Long): ApiResult<TenantVO> =
        ApiResult.success(TenantVOConverter.toTenantVO(tenantService.freeze(id)))

    @PostMapping("/{id}/unfreeze")
    fun unfreeze(@PathVariable("id") id: Long): ApiResult<TenantVO> =
        ApiResult.success(TenantVOConverter.toTenantVO(tenantService.unfreeze(id)))

    /** 整棵租户子树，支撑代理商看下级 */
    @GetMapping("/{id}/subtree")
    fun subtree(@PathVariable("id") id: Long): ApiResult<List<TenantVO>> =
        ApiResult.success(tenantService.subtree(id).map { TenantVOConverter.toTenantVO(it) })
}
