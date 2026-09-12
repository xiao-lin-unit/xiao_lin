package com.xiaolin.system.application.command

import com.xiaolin.shared.common.constants.Gender
import com.xiaolin.shared.common.constants.UserKind
import jakarta.validation.constraints.NotBlank


class UserRegisterCommand(

    /** 登录账号 */
    @field:NotBlank(message = "请输入登录账号")
    val username: String,

    /** 密码（BCrypt加密存储） */
    @field:NotBlank(message = "请输入密码")
    val password: String,

    /** 真实姓名 */
    @field:NotBlank(message = "请输入真实姓名")
    val realName: String? = null,

    /** 昵称 */
//    @field:NotBlank(message = "请输入昵称")
//    val nickname: String? = null,

    @field:NotBlank(message = "请输入头像地址")
    /** 头像地址 */
    val avatar: String? = null,

    /** 性别：0未知 1男 2女 */
    val gender: Int = Gender.UNKNOWN,

    @field:NotBlank(message = "请输入邮箱")
    /** 邮箱 */
    val email: String? = null,

    @field:NotBlank(message = "请输入手机号")
    /** 手机号 */
    val phone: String? = null,
    val idCardNo: String? = null,

    /** 用户类型：0普通用户 1租户管理员 2超级管理员 */
    val userKind: UserKind = UserKind.NORMAL,

    )