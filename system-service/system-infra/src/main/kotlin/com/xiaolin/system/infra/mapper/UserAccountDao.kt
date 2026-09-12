package com.xiaolin.system.infra.mapper

import com.xiaolin.shared.common.constants.UserKind
import com.xiaolin.shared.infra.dao.AbstractJdbcDao
import com.xiaolin.shared.infra.dao.paramsOf
import com.xiaolin.shared.infra.dao.rowMapperOf
import com.xiaolin.system.infra.persistence.UserAccount
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class UserAccountDao(jdbc: NamedParameterJdbcTemplate
): AbstractJdbcDao<UserAccount>(jdbc, UserAccount::class) {
    override val rowMapper: RowMapper<UserAccount> = rowMapperOf { rs ->
        UserAccount(
            id = rs.getLong("id"),
            username = rs.getString("username"),
            phone = rs.getString("phone"),
            email = rs.getString("email"),
            password = rs.getString("password"),
            realName = rs.getString("real_name"),
            idCardNo = rs.getBytes("id_card_no"),
            avatar = rs.getString("avatar"),
            gender = rs.getInt("gender"),
            userKind = UserKind.valueOf(rs.getString("user_kind")),
            status = rs.getInt("status"),
            pwdUpdatedAt = rs.getObject("pwd_updated_at", OffsetDateTime::class.java),
            pwdExpireAt = rs.getObject("pwd_expire_at", OffsetDateTime::class.java),
            lastLoginAt = rs.getObject("last_login_at", OffsetDateTime::class.java),
            lastLoginIp = rs.getString("last_login_ip"),
            loginFailCount = rs.getInt("login_fail_count"),
            lockedUntil = rs.getObject("locked_until", OffsetDateTime::class.java),
            version = rs.getInt("version"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            deletedAt = rs.getObject("deleted_at", OffsetDateTime::class.java),
            createdBy = rs.getLong("created_by"),
            updatedBy = rs.getLong("updated_by"),
            deletedBy = rs.getLong("deleted_by"),
            deleted = rs.getBoolean("deleted")
        )
    }

    fun findByUsername(username: String): UserAccount? {
        return queryOne(
            sql = "SELECT * FROM sys.user_account WHERE username = :username AND deleted = false",
            params = MapSqlParameterSource().addValue("username", username)
        )
    }


}