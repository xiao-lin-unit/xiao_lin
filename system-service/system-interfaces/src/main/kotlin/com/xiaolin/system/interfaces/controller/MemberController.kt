package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.MemberCreateCommand
import com.xiaolin.system.application.service.MemberService
import com.xiaolin.system.interfaces.converter.MemberVOConverter
import com.xiaolin.system.interfaces.vo.MemberVO
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
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: MemberCreateCommand): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: MemberCreateCommand,
    ): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        memberService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam tenantId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<MemberVO>> =
        ApiResult.success(
            memberService.list(tenantId, PageRequest.of(page, size)).map { MemberVOConverter.toMemberVO(it) }
        )

    /** 某用户的全部租户身份，用于"切换租户"下拉 */
    @GetMapping("/by-user")
    fun listByUserId(@RequestParam userId: Long): ApiResult<List<MemberVO>> =
        ApiResult.success(memberService.listByUserId(userId).map { MemberVOConverter.toMemberVO(it) })

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable("id") id: Long): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.approve(id)))

    @PostMapping("/{id}/disable")
    fun disable(@PathVariable("id") id: Long): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.disable(id)))

    @PostMapping("/{id}/quit")
    fun quit(@PathVariable("id") id: Long): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.quit(id)))

    @PostMapping("/{id}/grant-tenant-admin")
    fun grantTenantAdmin(@PathVariable("id") id: Long): ApiResult<MemberVO> =
        ApiResult.success(MemberVOConverter.toMemberVO(memberService.grantTenantAdmin(id)))
}
