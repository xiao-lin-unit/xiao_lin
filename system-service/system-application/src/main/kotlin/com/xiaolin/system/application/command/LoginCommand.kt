package com.xiaolin.system.application.command

import com.xiaolin.system.domain.model.entity.LoginTypeEnum

class LoginCommand(
    val username: String?,

    val password: String?,

    val code: String?,

    val openId: String?,

    val phone: String?,

    val email: String?,

    val type: LoginTypeEnum = LoginTypeEnum.PASSWORD
) {
}
