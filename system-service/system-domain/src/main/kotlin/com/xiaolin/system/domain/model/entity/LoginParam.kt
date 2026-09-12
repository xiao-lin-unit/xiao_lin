package com.xiaolin.system.domain.model.entity

import com.xiaolin.system.domain.aggregation.UserAggregation

enum class LoginTypeEnum {
    PASSWORD,
    PHONE,
    EMAIL,
    WECHAT,
    QQ,
    DINGTALK
}

sealed class LoginParam(
    val type: LoginTypeEnum,
    val ip: String?
) {
    abstract fun verify(user: UserAggregation): Boolean
}

class PasswordLoginParam(
    val username: String,
    val password: String,
    val match: (rawPassword: String, encodedPassword: String) -> Boolean,
    ip: String? = null
): LoginParam(type = LoginTypeEnum.PASSWORD, ip = ip) {
    override fun verify(user: UserAggregation): Boolean = match(password, user.password)

}

class PhoneLoginParam(
    val phone: String,
    val code: String,
    ip: String? = null
): LoginParam(LoginTypeEnum.PHONE, ip = ip) {
    override fun verify(user: UserAggregation): Boolean {
        TODO("Not yet implemented")
    }
}

class EmailLoginParam(
    val email: String,
    val code: String,
    ip: String? = null
): LoginParam(LoginTypeEnum.EMAIL, ip = ip) {
    override fun verify(user: UserAggregation): Boolean {
        TODO("Not yet implemented")
    }
}

class WechatLoginParam(
    val openid: String,
    val code: String,
    ip: String? = null
): LoginParam(LoginTypeEnum.WECHAT, ip = ip) {
    override fun verify(user: UserAggregation): Boolean {
        TODO("Not yet implemented")
    }
}

class QQLoginParam(
    val openid: String,
    val code: String,
    ip: String? = null
): LoginParam(LoginTypeEnum.QQ, ip = ip) {
    override fun verify(user: UserAggregation): Boolean {
        TODO("Not yet implemented")
    }
}

class DingtalkLoginParam(
    val openid: String,
    val code: String,
    ip: String? = null
): LoginParam(LoginTypeEnum.DINGTALK, ip = ip) {
    override fun verify(user: UserAggregation): Boolean {
        TODO("Not yet implemented")
    }
}
