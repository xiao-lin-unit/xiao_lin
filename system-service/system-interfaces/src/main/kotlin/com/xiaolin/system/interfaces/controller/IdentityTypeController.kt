package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.IdentityTypeCreateCommand
import com.xiaolin.system.application.service.IdentityTypeService
import com.xiaolin.system.interfaces.converter.IdentityTypeVOConverter
import com.xiaolin.system.interfaces.vo.IdentityTypeVO
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
@RequestMapping("/identity-type")
class IdentityTypeController(
    private val identityTypeService: IdentityTypeService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: IdentityTypeCreateCommand): ApiResult<IdentityTypeVO> =
        ApiResult.success(IdentityTypeVOConverter.toIdentityTypeVO(identityTypeService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: IdentityTypeCreateCommand,
    ): ApiResult<IdentityTypeVO> =
        ApiResult.success(IdentityTypeVOConverter.toIdentityTypeVO(identityTypeService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        identityTypeService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<IdentityTypeVO> =
        ApiResult.success(IdentityTypeVOConverter.toIdentityTypeVO(identityTypeService.getById(id)))

    /** tenantTypeId 为空时只返回通用身份；有值时返回「通用 + 该类型专属」 */
    @GetMapping("/list")
    fun list(
        @RequestParam(required = false) tenantTypeId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<IdentityTypeVO>> =
        ApiResult.success(
            identityTypeService.list(tenantTypeId, PageRequest.of(page, size))
                .map { IdentityTypeVOConverter.toIdentityTypeVO(it) }
        )
}
