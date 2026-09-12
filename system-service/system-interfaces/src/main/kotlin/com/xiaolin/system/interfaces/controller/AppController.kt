package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.AppCreateCommand
import com.xiaolin.system.application.service.AppService
import com.xiaolin.system.interfaces.converter.AppVOConverter
import com.xiaolin.system.interfaces.vo.AppVO
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
@RequestMapping("/app")
class AppController(
    private val appService: AppService,
) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid command: AppCreateCommand): ApiResult<AppVO> =
        ApiResult.success(AppVOConverter.toAppVO(appService.create(command)))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid command: AppCreateCommand,
    ): ApiResult<AppVO> =
        ApiResult.success(AppVOConverter.toAppVO(appService.update(id, command)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ApiResult<Nothing?> {
        appService.delete(id)
        return ApiResult.success()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<AppVO> =
        ApiResult.success(AppVOConverter.toAppVO(appService.getById(id)))

    @GetMapping("/list")
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResult<Page<AppVO>> =
        ApiResult.success(appService.list(PageRequest.of(page, size)).map { AppVOConverter.toAppVO(it) })
}
