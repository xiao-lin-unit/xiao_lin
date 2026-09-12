package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.common.constants.UserKind
import com.xiaolin.shared.infra.dao.SqlType
import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.sql.Types
import java.time.OffsetDateTime

/**
 * 用户：自然人，全局唯一，登录主体
 *
 * 注：idCardNo 为 ByteArray，data class 自动生成的 equals/hashCode 对它走引用比较。
 *     若业务上需要"两次查询出同一条用户记录视为相等"，请自行按 id 比较，
 *     或在读取后把 idCardNo 解密为 String 再参与判等。
 */
@Table("sys.user_account")
class UserAccount(
    id: Long,
    @Column("username")
    val username: String,
    @Column("phone")
    val phone: String? = null,
    @Column("email")
    val email: String? = null,
    @Column("password")
    val password: String? = null,
    @Column("real_name")
    val realName: String? = null,
    @Column("id_card_no")
    val idCardNo: ByteArray? = null,
    @Column("avatar")
    val avatar: String? = null,
    /**
     * 性别：0-未知 1-男 2-女
     */
    @Column("gender")
    val gender: Int,
    /**
     * 用户类型：NORMAL 普通 / SUPER_ADMIN 超管 / SYSTEM 系统账号
     */
    @Column("user_kind")
    val userKind: UserKind,
    /**
     * 状态：1正常 0未激活 2冻结 3注销
     */
    @Column("status")
    val status: Int,
    @Column("pwd_updated_at")
    val pwdUpdatedAt: OffsetDateTime? = null,
    @Column("pwd_expire_at")
    val pwdExpireAt: OffsetDateTime? = null,
    @Column("last_login_at")
    val lastLoginAt: OffsetDateTime? = null,
    @Column("last_login_ip")
    @SqlType(Types.OTHER)
    val lastLoginIp: String? = null,
    @Column("login_fail_count")
    val loginFailCount: Int = 0,
    @Column("locked_until")
    val lockedUntil: OffsetDateTime? = null,
    createdAt: OffsetDateTime? = null,
    updatedAt: OffsetDateTime? = null,
    deletedAt: OffsetDateTime? = null,
    createdBy: Long? = null,
    updatedBy: Long? = null,
    deletedBy: Long? = null,
    deleted: Boolean = false,
): BaseEntity(
    id = id,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    createdBy = createdBy,
    updatedBy = updatedBy,
    deletedBy = deletedBy,
    deleted = deleted
) {
    constructor() : this(
        id = 0L,
        username = "",
        phone = null,
        email = null,
        password = null,
        realName = null,
        avatar = null,
        gender = 0,
        userKind = UserKind.NORMAL,
        status = 1,
        pwdUpdatedAt = null,
        pwdExpireAt = null,
        lastLoginAt = null,
        lastLoginIp = null,
        loginFailCount = 0,
        lockedUntil = null,
        createdAt = OffsetDateTime.now(),
        updatedAt = null,
        deletedAt = null,
        createdBy = null,
        updatedBy = null,
        deletedBy = null,
        deleted = false
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAccount

        if (id != other.id) return false
        if (gender != other.gender) return false
        if (status != other.status) return false
        if (loginFailCount != other.loginFailCount) return false
        if (createdBy != other.createdBy) return false
        if (updatedBy != other.updatedBy) return false
        if (deleted != other.deleted) return false
        if (deletedBy != other.deletedBy) return false
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
        var result = id?.hashCode() ?: 0
        result = 31 * result + (gender ?: 0)
        result = 31 * result + (status ?: 0)
        result = 31 * result + (loginFailCount ?: 0)
        result = 31 * result + (createdBy?.hashCode() ?: 0)
        result = 31 * result + (updatedBy?.hashCode() ?: 0)
        result = 31 * result + (deleted.hashCode() ?: 0)
        result = 31 * result + (deletedBy?.hashCode() ?: 0)
        result = 31 * result + (username.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (realName?.hashCode() ?: 0)
        result = 31 * result + (idCardNo?.contentHashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (userKind.hashCode() ?: 0)
        result = 31 * result + (pwdUpdatedAt?.hashCode() ?: 0)
        result = 31 * result + (pwdExpireAt?.hashCode() ?: 0)
        result = 31 * result + (lastLoginAt?.hashCode() ?: 0)
        result = 31 * result + (lastLoginIp?.hashCode() ?: 0)
        result = 31 * result + (lockedUntil?.hashCode() ?: 0)
        result = 31 * result + (createdAt.hashCode() ?: 0)
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + (deletedAt?.hashCode() ?: 0)
        return result
    }
}


/** 5.3 用户持有的全局角色；超管用户绑定 isSuper 的全局角色 */
data class UserGlobalRole(
    val id: Long = 0,
    val userId: Long,
    val globalRoleId: Long,
    val grantedBy: Long? = null,
    val createdAt: OffsetDateTime,
)
