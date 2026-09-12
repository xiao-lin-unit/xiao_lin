package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.GlobalRoleCreateCommand
import com.xiaolin.system.application.service.GlobalRoleService
import com.xiaolin.system.interfaces.converter.GlobalRoleVOConverter
import com.xiaolin.system.interfaces.vo.GlobalRoleVO
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
@RequestMapping("/global-role")
class GlobalRoleController(
    private val globalRoleService: GlobalRoleService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: GlobalRoleCreateCommand): ApiResult<GlobalRoleVO> =
        ApiResult.success(GlobalRoleVOConverter.toGlobalRoleVO(globalRoleService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: GlobalRoleCreateCommand,
    ): ApiResult<GlobalRoleVO> =
        ApiResult.success(GlobalRoleVOConverter.toGlobalRoleVO(globalRoleService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        globalRoleService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<GlobalRoleVO> =
        ApiResult.success(GlobalRoleVOConverter.toGlobalRoleVO(globalRoleService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<GlobalRoleVO>> =
        ApiResult.success(globalRoleService.list(PageRequest.of(page, size)).map { GlobalRoleVOConverter.toGlobalRoleVO(it) })
}
