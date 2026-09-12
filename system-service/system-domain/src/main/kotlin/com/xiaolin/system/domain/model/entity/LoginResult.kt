package com.xiaolin.system.domain.model.entity

open class LoginResult(
    val success: Boolean = true,
    val reason: LoginFailReason? = null,
    val message: String = "登录成功"
)

class LoginFailure(reason: LoginFailReason, message: String): LoginResult(success = false, reason = reason, message = message)

class LoginSuccess(reason: LoginFailReason? = null, message: String = "登录成功"): LoginResult(success = true, reason = reason, message = message)

enum class LoginFailReason {
    BAD_CREDENTIALS,
    LOCKED,
    EXPIRED,
    NOT_FOUND,
    DISABLED
}