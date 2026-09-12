package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.RoleCreateCommand
import com.xiaolin.system.application.service.RoleService
import com.xiaolin.system.interfaces.converter.RoleVOConverter
import com.xiaolin.system.interfaces.vo.RoleVO
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
@RequestMapping("/role")
class RoleController(
    private val roleService: RoleService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: RoleCreateCommand): ApiResult<RoleVO> =
        ApiResult.success(RoleVOConverter.toRoleVO(roleService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: RoleCreateCommand,
    ): ApiResult<RoleVO> =
        ApiResult.success(RoleVOConverter.toRoleVO(roleService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        roleService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<RoleVO> =
        ApiResult.success(RoleVOConverter.toRoleVO(roleService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam tenantId: Long,
        @RequestParam(required = false) appId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<RoleVO>> =
        ApiResult.success(
            roleService.list(tenantId, appId, PageRequest.of(page, size)).map { RoleVOConverter.toRoleVO(it) }
        )

    /** 角色继承链：[自身, 父, 祖父, ...]，权限快照计算时按此顺序合并 */
    @GetMapping("/{id}/inheritance")
    fun inheritance(@PathVariable("id") id: Long): ApiResult<List<RoleVO>> =
        ApiResult.success(roleService.inheritanceChain(id).map { RoleVOConverter.toRoleVO(it) })
}
