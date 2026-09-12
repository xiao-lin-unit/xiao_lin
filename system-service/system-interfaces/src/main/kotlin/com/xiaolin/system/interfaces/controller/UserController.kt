package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.UserRegisterCommand
import com.xiaolin.system.application.service.UserService
import com.xiaolin.system.interfaces.converter.UserVOConverter
import com.xiaolin.system.interfaces.vo.UserVO
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid user: UserRegisterCommand): ApiResult<UserVO> {
        val registerUser = userService.registerUser(user)
        return ApiResult.success(UserVOConverter.toUserVO(registerUser))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): ApiResult<UserVO> {
        val user = userService.getById(id)
        return ApiResult.success(UserVOConverter.toUserVO(user))
    }

    @GetMapping("/list")
    fun list(): ApiResult<Page<UserVO>> {
        val users = userService.list()
        return ApiResult.success(users.map { UserVOConverter.toUserVO(it) })
    }


}