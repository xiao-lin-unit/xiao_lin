package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.TenantTypeCreateCommand
import com.xiaolin.system.application.service.TenantTypeService
import com.xiaolin.system.interfaces.converter.TenantTypeVOConverter
import com.xiaolin.system.interfaces.vo.TenantTypeVO
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
@RequestMapping("/tenant-type")
class TenantTypeController(
    private val tenantTypeService: TenantTypeService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: TenantTypeCreateCommand): ApiResult<TenantTypeVO> =
        ApiResult.success(TenantTypeVOConverter.toTenantTypeVO(tenantTypeService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: TenantTypeCreateCommand,
    ): ApiResult<TenantTypeVO> =
        ApiResult.success(TenantTypeVOConverter.toTenantTypeVO(tenantTypeService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        tenantTypeService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<TenantTypeVO> =
        ApiResult.success(TenantTypeVOConverter.toTenantTypeVO(tenantTypeService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<TenantTypeVO>> =
        ApiResult.success(tenantTypeService.list(PageRequest.of(page, size)).map { TenantTypeVOConverter.toTenantTypeVO(it) })
}
