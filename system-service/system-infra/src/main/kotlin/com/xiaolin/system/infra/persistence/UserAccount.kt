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

}


/** 5.3 用户持有的全局角色；超管用户绑定 isSuper 的全局角色 */
data class UserGlobalRole(
    val id: Long = 0,
    val userId: Long,
    val globalRoleId: Long,
    val grantedBy: Long? = null,
    val createdAt: OffsetDateTime,
)
