package com.xiaolin.system.application.command

import com.xiaolin.shared.common.constants.Gender
import com.xiaolin.shared.common.constants.UserKind
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern


class UserRegisterCommand(

    /** 登录账号 */
    @field:NotBlank(message = "请输入登录账号")
    val username: String,

    /** 密码（BCrypt加密存储） */
    @field:NotBlank(message = "请输入密码")
    val password: String,

    /** 真实姓名 */
//    @field:NotBlank(message = "请输入真实姓名")
    val realName: String? = null,

    /** 昵称 */
//    @field:NotBlank(message = "请输入昵称")
//    val nickname: String? = null,

//    @field:NotBlank(message = "请输入头像地址")
    /** 头像地址,空则使用系统默认头像 */
    val avatar: String? = null,

    /** 性别：0未知 1男 2女 */
    val gender: Int = Gender.UNKNOWN,

    @field:Email(message = "请输入邮箱")
    /** 邮箱 */
    val email: String? = null,

    @field:Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    /** 手机号 */
    val phone: String,
    val idCardNo: String? = null,

    /** 用户类型：0普通用户 1租户管理员 2超级管理员 */
    val userKind: UserKind = UserKind.NORMAL,

    )