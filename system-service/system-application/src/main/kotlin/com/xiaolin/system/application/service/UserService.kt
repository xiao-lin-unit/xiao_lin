package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.LoginCommand
import com.xiaolin.system.application.command.UserRegisterCommand
import com.xiaolin.system.application.security.JwtTokenUtil
import com.xiaolin.system.domain.aggregation.UserAggregation
import com.xiaolin.system.domain.model.entity.*
import com.xiaolin.system.domain.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private fun LoginFailReason.toException(msg: String): AuthenticationException = when (this) {
    LoginFailReason.NOT_FOUND       -> BadCredentialsException(msg)
    LoginFailReason.BAD_CREDENTIALS -> BadCredentialsException(msg)   // 与 NOT_FOUND 同文案
    LoginFailReason.LOCKED          -> LockedException(msg)
    LoginFailReason.DISABLED        -> DisabledException(msg)
    LoginFailReason.EXPIRED         -> AccountExpiredException(msg)
}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val idGenerator: IdGenerator,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenUtil: JwtTokenUtil
) {

    fun registerUser(command: UserRegisterCommand): UserAggregation {

        val nextId = idGenerator.nextId()

        val userAggregation = UserAggregation.create(nextId)
            .username(command.username)
            .password(passwordEncoder.encode(command.password)!!)
            .realName(command.realName)
            .avatar(command.avatar)
            .gender(command.gender)
            .email(command.email)
            .phone(command.phone)
            .userKind(command.userKind)
            .idCardNo(command.idCardNo)
            .register()

        userRepository.create(userAggregation)

        return userAggregation

    }

    fun getById(id: Long): UserAggregation {
        val user = userRepository.getById(id)
        return user
    }

    fun list(): Page<UserAggregation> {
        return userRepository.list()
    }

    fun login(loginCommand: LoginCommand): String {

        val loginParamDTO = loadLoginDTO(loginCommand)
        val user = loginParamDTO.user ?: throw BadCredentialsException("登录失败,请检查登录信息")
        val result = loginParamDTO.user.doLogin(loginParamDTO.loginParam)
        userRepository.update(user)
        if (result.success) {
            // TODO 权限和租户暂时用固定值,待角色权限和租户内容完成后修改
            return jwtTokenUtil.encode(
                user.id.toString(),
                mapOf(
                    "username" to user.username,
                    "roles" to emptyList<String>(),
                    "tenantId" to 0L,
                    "userKind" to user.userKind.name
                )
            ).tokenValue
        }
        throw result.reason?.toException(result.message) ?: BadCredentialsException("登录失败,请检查登录信息")
    }


    private fun loadLoginDTO(loginCommand: LoginCommand): LoginParamDTO {
        return when (loginCommand.type) {
            LoginTypeEnum.PASSWORD -> {
                LoginParamDTO(
                    loginParam = PasswordLoginParam(loginCommand.username!!, loginCommand.password!!, passwordEncoder::matches),
                    user = userRepository.getByUsername(loginCommand.username)
                )
            }
            LoginTypeEnum.PHONE -> {
                // TODO 手机登录, 待后续实现
                LoginParamDTO(
                    loginParam = PhoneLoginParam(loginCommand.phone!!, loginCommand.code!!),
                    user = null
                )
            }
            LoginTypeEnum.EMAIL -> {
                // TODO 邮箱登录, 待后续实现
                LoginParamDTO(
                    loginParam = EmailLoginParam(loginCommand.email!!, loginCommand.code!!),
                    user = null
                )
            }
            LoginTypeEnum.WECHAT -> {
                // TODO 微信登录, 待后续实现
                LoginParamDTO(
                    loginParam = WechatLoginParam(loginCommand.openId!!, loginCommand.code!!),
                    user = null
                )
            }
            LoginTypeEnum.QQ -> {
                // TODO QQ登录, 待后续实现
                LoginParamDTO(
                    loginParam = QQLoginParam(loginCommand.openId!!, loginCommand.code!!),
                    user = null
                )
            }
            LoginTypeEnum.DINGTALK -> {
                // TODO 钉钉登录, 待后续实现
                LoginParamDTO(
                    loginParam = DingtalkLoginParam(loginCommand.openId!!, loginCommand.code!!),
                    user = null
                )
            }
        }
    }


}

private class LoginParamDTO(
    val loginParam: LoginParam,
    val user: UserAggregation?
)