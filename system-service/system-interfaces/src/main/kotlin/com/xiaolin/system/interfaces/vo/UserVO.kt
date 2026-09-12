package com.xiaolin.system.interfaces.vo

import com.xiaolin.shared.common.constants.UserKind

data class UserVO(
    val id: Long? = null,
    val username: String,
    val phone: String? = null,
    val email: String? = null,
    val realName: String? = null,
    val avatar: String? = null,
    /**
     * 性别：0-未知 1-男 2-女
     */
    val gender: Int,
    /**
     * 用户类型：NORMAL 普通 / SUPER_ADMIN 超管 / SYSTEM 系统账号
     */
    val userKind: UserKind,
    /**
     * 状态：1正常 0未激活 2冻结 3注销
     */
    var status: Int,

)
