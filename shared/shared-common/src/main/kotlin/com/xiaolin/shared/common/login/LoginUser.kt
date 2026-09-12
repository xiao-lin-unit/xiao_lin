package com.xiaolin.shared.common.login

import com.xiaolin.shared.common.constants.UserKind

// security/LoginUser.kt
data class LoginUserInfo(
    val userId: Long,
    val username: String,
    val tenantId: Long? = null,
    val roles: Set<String> = emptySet(),
    val enabled: Boolean = true,
    val userKind: UserKind = UserKind.NORMAL,
) {
    companion object {
        fun of(userId: Long, username: String, tenantId: Long? = null, roles: Set<String> = emptySet(), enabled: Boolean, userKind: UserKind) =
            LoginUserInfo(userId, username, tenantId, roles, enabled, userKind)

        fun create(userId: Long, username: String, tenantId: Long? = null, roles: Set<String> = emptySet(), userKind: UserKind = UserKind.NORMAL) =
            of(userId, username, tenantId, roles, true, userKind)

        fun anonymous(userId: Long) = LoginUserInfo(userId, "anonymous")
        fun disabled(userId: Long) = LoginUserInfo(userId, "unknown", enabled = false)
    }
}


