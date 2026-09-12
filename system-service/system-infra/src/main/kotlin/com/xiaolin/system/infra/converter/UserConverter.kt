package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.UserAggregation
import com.xiaolin.system.infra.persistence.UserAccount

object UserConverter {

    @JvmStatic
    fun aggregation2DO(userAggregation: UserAggregation): UserAccount {
        return UserAccount(
            id = userAggregation.id,
            username = userAggregation.username,
            password = userAggregation.password,
            realName = userAggregation.realName,
            idCardNo = userAggregation.idCardNo,
            avatar = userAggregation.avatar,
            gender = userAggregation.gender,
            email = userAggregation.email,
            phone = userAggregation.phone,
            userKind = userAggregation.userKind,
            status = userAggregation.status,
            lastLoginIp = userAggregation.lastLoginIp,
            lastLoginAt = userAggregation.lastLoginAt,
            pwdUpdatedAt = userAggregation.pwdUpdatedAt,
            pwdExpireAt = userAggregation.pwdExpireAt,
            loginFailCount = userAggregation.loginFailCount,
            lockedUntil = userAggregation.lockedUntil,
            version = userAggregation.version,
            createdAt = userAggregation.createdAt,
            updatedAt = userAggregation.updatedAt,
            deletedAt = userAggregation.deletedAt,
            createdBy = userAggregation.createdBy,
            updatedBy = userAggregation.updatedBy,
            deletedBy = userAggregation.deletedBy,
            deleted = userAggregation.deleted
        )
    }

    @JvmStatic
    fun do2Aggregation(userAccount: UserAccount): UserAggregation {
        return UserAggregation.builder(userAccount.id)
            .username(userAccount.username)
            .password(userAccount.password ?: "")
            .phone(userAccount.phone)
            .email(userAccount.email)
            .realName(userAccount.realName)
            .idCardNo(userAccount.idCardNo)
            .avatar(userAccount.avatar)
            .gender(userAccount.gender)
            .userKind(userAccount.userKind)
            .status(userAccount.status)
            .pwdUpdatedAt(userAccount.pwdUpdatedAt)
            .pwdExpireAt(userAccount.pwdExpireAt)
            .lastLoginAt(userAccount.lastLoginAt)
            .lastLoginIp(userAccount.lastLoginIp)
            .loginFailCount(userAccount.loginFailCount)
            .lockedUntil(userAccount.lockedUntil)
            .version(userAccount.version)
            .createdAt(userAccount.createdAt)
            .updatedAt(userAccount.updatedAt)
            .deletedAt(userAccount.deletedAt)
            .createdBy(userAccount.createdBy)
            .updatedBy(userAccount.updatedBy)
            .deletedBy(userAccount.deletedBy)
            .deleted(userAccount.deleted)
            .build()
    }

}