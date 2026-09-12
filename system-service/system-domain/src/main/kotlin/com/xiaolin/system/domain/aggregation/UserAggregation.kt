package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.Gender
import com.xiaolin.shared.common.constants.UserKind
import com.xiaolin.shared.common.constants.UserStatus
import com.xiaolin.shared.common.provider.TimeProvider
import com.xiaolin.system.domain.model.entity.DingtalkLoginParam
import com.xiaolin.system.domain.model.entity.EmailLoginParam
import com.xiaolin.system.domain.model.entity.LoginFailReason
import com.xiaolin.system.domain.model.entity.LoginFailure
import com.xiaolin.system.domain.model.entity.LoginParam
import com.xiaolin.system.domain.model.entity.LoginResult
import com.xiaolin.system.domain.model.entity.LoginSuccess
import com.xiaolin.system.domain.model.entity.PasswordLoginParam
import com.xiaolin.system.domain.model.entity.PhoneLoginParam
import com.xiaolin.system.domain.model.entity.QQLoginParam
import com.xiaolin.system.domain.model.entity.WechatLoginParam
import java.time.Clock
import java.time.OffsetDateTime

data class UserAggregation(
    val id: Long,
    val username: String,
    val phone: String? = null,
    val email: String? = null,
    var password: String,
    val realName: String? = null,
    val idCardNo: ByteArray? = null,
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
    var pwdUpdatedAt: OffsetDateTime? = null,
    val pwdExpireAt: OffsetDateTime? = null,
    var lastLoginAt: OffsetDateTime? = null,
    var lastLoginIp: String? = null,
    var loginFailCount: Int = 0,
    var lockedUntil: OffsetDateTime? = null,
    val createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    val createdBy: Long? = null,
    val updatedBy: Long? = null,
    val deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class UserAggregationBuilder() {
        var id: Long = 0L
        lateinit var username: String
        var phone: String? = null
        var email: String? = null
        lateinit var password: String
        var realName: String? = null
        var idCardNo: ByteArray? = null
        var avatar: String? = null
        var gender: Int = Gender.UNKNOWN
        lateinit var userKind: UserKind
        var status: Int = UserStatus.INACTIVE
        var pwdUpdatedAt: OffsetDateTime? = null
        var pwdExpireAt: OffsetDateTime? = null
        var lastLoginAt: OffsetDateTime? = null
        var lastLoginIp: String? = null
        var loginFailCount: Int = 0
        var lockedUntil: OffsetDateTime? = null
        var createdAt: OffsetDateTime? = null
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun username(username: String) = apply { this.username = username }
        fun password(password: String) = apply { this.password = password }
        fun phone(phone: String?) = apply { this.phone = phone }
        fun email(email: String?) = apply { this.email = email }
        fun realName(realName: String?) = apply { this.realName = realName }
        fun idCardNo(idCardNo: String?) = apply { this.idCardNo = idCardNo?.toByteArray(Charsets.UTF_8) }
        fun idCardNo(idCardNo: ByteArray?) = apply { this.idCardNo = idCardNo }
        fun avatar(avatar: String?) = apply { this.avatar = avatar }
        fun gender(gender: Int) = apply { this.gender = gender }
        fun userKind(userKind: UserKind) = apply { this.userKind = userKind }
        fun status(status: Int) = apply { this.status = status }
        fun pwdUpdatedAt(pwdUpdatedAt: OffsetDateTime?) = apply { this.pwdUpdatedAt = pwdUpdatedAt }
        fun pwdExpireAt(pwdExpireAt: OffsetDateTime?) = apply { this.pwdExpireAt = pwdExpireAt }
        fun lastLoginAt(lastLoginAt: OffsetDateTime?) = apply { this.lastLoginAt = lastLoginAt }
        fun lastLoginIp(lastLoginIp: String?) = apply { this.lastLoginIp = lastLoginIp }
        fun loginFailCount(loginFailCount: Int) = apply { this.loginFailCount = loginFailCount }
        fun lockedUntil(lockedUntil: OffsetDateTime?) = apply { this.lockedUntil = lockedUntil }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }

        fun deleted(deleted: Boolean) = apply { this.deleted = deleted }

        fun register(): UserAggregation {
            val user = build()
            user.register()
            return user;
        }

        fun build(): UserAggregation {
            return UserAggregation(
                id = id,
                username = username,
                phone = phone,
                email = email,
                password = password,
                realName = realName,
                idCardNo = idCardNo,
                avatar = avatar,
                gender = gender,
                userKind = userKind,
                status = status,
                pwdUpdatedAt = pwdUpdatedAt,
                pwdExpireAt = pwdExpireAt,
                lastLoginAt = lastLoginAt,
                lastLoginIp = lastLoginIp,
                loginFailCount = loginFailCount,
                lockedUntil = lockedUntil,
                createdBy = createdBy,
                createdAt = createdAt,
                updatedBy = updatedBy,
                updatedAt = updatedAt,
                deleted = deleted,
                deletedBy = deletedBy,
                deletedAt = deletedAt
            )
        }

    }

    companion object {

        fun create(id: Long): UserAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .pwdUpdatedAt(now)
                .pwdExpireAt(now.plusYears(1L))
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): UserAggregationBuilder {
            return UserAggregationBuilder().id(id)
        }

    }

    fun register() {
        validate()
//        TODO("添加注册事件")
    }

    private fun validate() {
//        TODO("添加验证逻辑")
    }

    private fun touch(now: OffsetDateTime = TimeProvider.now()) = apply {
        this.updatedAt = now
    }

    fun disable() = apply {
        this.status = 0
        this.touch()
    }

    fun lock() = apply {
        if (this.loginFailCount > 3) {
            this.status = 2
            this.lockedUntil = TimeProvider.now().plusMinutes(30 * (this.loginFailCount - 3).toLong())
            this.touch()
        }
    }

    fun unlock() = apply {
        this.status = 1
        this.lockedUntil = null
        this.touch()
    }

    /**
     * 预解锁
     * 登录时先做一次预解锁操作
     */
    fun preUnlock() = apply {
        if (isLocked() && this.loginFailCount > 3 && this.lockedUntil?.isBefore(TimeProvider.now()) == true) {
            unlock()
        }
    }

    fun loginFail() = apply {
        this.loginFailCount++
        lock()
        this.touch()
    }

    fun loginSuccess(ip: String?) = apply {
        val now = TimeProvider.now()
        this.loginFailCount = 0
        this.lastLoginIp = ip
        this.lastLoginAt = now
        unlock()
        this.touch(now)
    }

    fun del() = apply {
        val now = TimeProvider.now()
        this.deleted = true
        this.deletedAt = now
        this.touch(now)
    }

    fun updatePwd(password: String) = apply {
        val now = TimeProvider.now()
        this.password = password
        this.pwdUpdatedAt = now
        this.touch(now)
    }

    fun isDeleted(): Boolean = this.deleted

    fun isLocked(): Boolean = this.status == 2

    fun isDisabled(): Boolean = this.status == 0

    fun isExpired(): Boolean = this.pwdExpireAt?.isBefore(TimeProvider.now()) ?: false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAggregation

        if (id != other.id) return false
        if (gender != other.gender) return false
        if (status != other.status) return false
        if (loginFailCount != other.loginFailCount) return false
        if (createdBy != other.createdBy) return false
        if (updatedBy != other.updatedBy) return false
        if (deletedBy != other.deletedBy) return false
        if (deleted != other.deleted) return false
        if (username != other.username) return false
        if (phone != other.phone) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (realName != other.realName) return false
        if (!idCardNo.contentEquals(other.idCardNo)) return false
        if (avatar != other.avatar) return false
        if (userKind != other.userKind) return false
        if (pwdUpdatedAt != other.pwdUpdatedAt) return false
        if (pwdExpireAt != other.pwdExpireAt) return false
        if (lastLoginAt != other.lastLoginAt) return false
        if (lastLoginIp != other.lastLoginIp) return false
        if (lockedUntil != other.lockedUntil) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (deletedAt != other.deletedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode() ?: 0
        result = 31 * result + (gender ?: 0)
        result = 31 * result + (status ?: 0)
        result = 31 * result + (loginFailCount ?: 0)
        result = 31 * result + (createdBy?.hashCode() ?: 0)
        result = 31 * result + (updatedBy?.hashCode() ?: 0)
        result = 31 * result + (deletedBy?.hashCode() ?: 0)
        result = 31 * result + deleted.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (password.hashCode() ?: 0)
        result = 31 * result + (realName?.hashCode() ?: 0)
        result = 31 * result + (idCardNo?.contentHashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (userKind.hashCode() ?: 0)
        result = 31 * result + (pwdUpdatedAt?.hashCode() ?: 0)
        result = 31 * result + (pwdExpireAt?.hashCode() ?: 0)
        result = 31 * result + (lastLoginAt?.hashCode() ?: 0)
        result = 31 * result + (lastLoginIp?.hashCode() ?: 0)
        result = 31 * result + (lockedUntil?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + (deletedAt?.hashCode() ?: 0)
        return result
    }

    fun doLogin(param: LoginParam): LoginResult {
        if (isDeleted()) {
            return LoginFailure(reason = LoginFailReason.NOT_FOUND, message = "登录失败,请检查登录信息")
        }
        if (isDisabled()) {
            return LoginFailure(reason = LoginFailReason.DISABLED, message = "登录失败,请检查登录信息")
        }
        preUnlock()
        if (isLocked()) {
            return LoginFailure(reason = LoginFailReason.LOCKED, message = "用户尝试登录次数超过限制,请稍后重试")
        }
//        if (isExpired()) {
//            error("用户密码已过期")
//        }
        if (param.verify(user = this)) {
            loginSuccess(param.ip)
            return LoginSuccess()
        } else {
            loginFail()
            return LoginFailure(reason = LoginFailReason.BAD_CREDENTIALS, message = "登录失败,请检查登录信息")
        }
    }


}


