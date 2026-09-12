package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.OrgCreateCommand
import com.xiaolin.system.application.service.OrgService
import com.xiaolin.system.interfaces.converter.OrgVOConverter
import com.xiaolin.system.interfaces.vo.OrgVO
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
@RequestMapping("/org")
class OrgController(
    private val orgService: OrgService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: OrgCreateCommand): ApiResult<OrgVO> =
        ApiResult.success(OrgVOConverter.toOrgVO(orgService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: OrgCreateCommand,
    ): ApiResult<OrgVO> =
        ApiResult.success(OrgVOConverter.toOrgVO(orgService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        orgService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<OrgVO> =
        ApiResult.success(OrgVOConverter.toOrgVO(orgService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam tenantId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<OrgVO>> =
        ApiResult.success(
            orgService.list(tenantId, PageRequest.of(page, size)).map { OrgVOConverter.toOrgVO(it) }
        )

    /** 直接子节点，前端树懒加载用；parentId 为空取根节点 */
    @GetMapping("/children")
    fun children(
        @RequestParam tenantId: Long,
        @RequestParam(required = false) parentId: Long?,
    ): ApiResult<List<OrgVO>> =
        ApiResult.success(orgService.children(tenantId, parentId).map { OrgVOConverter.toOrgVO(it) })

    /** 整棵子树，支撑 ORG_TREE 数据范围 */
    @GetMapping("/{id}/subtree")
    fun subtree(@PathVariable("id") id: Long): ApiResult<List<OrgVO>> =
        ApiResult.success(orgService.subtree(id).map { OrgVOConverter.toOrgVO(it) })
}
