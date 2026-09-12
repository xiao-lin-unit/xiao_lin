package com.xiaolin.system.interfaces.controller

import com.xiaolin.shared.interfaces.model.ApiResult
import com.xiaolin.system.application.command.LoginCommand
import com.xiaolin.system.application.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class LoginController(
    private val userService: UserService
) {


    @PostMapping("/login")
    fun login(@RequestBody loginCommand: LoginCommand): ApiResult<String> {
        val login = userService.login(loginCommand)
        return ApiResult.success(login);
    }
}