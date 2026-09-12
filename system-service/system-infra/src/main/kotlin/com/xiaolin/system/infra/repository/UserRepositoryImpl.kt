package com.xiaolin.system.infra.repository

import com.xiaolin.system.domain.aggregation.UserAggregation
import com.xiaolin.system.domain.repository.UserRepository
import com.xiaolin.system.infra.converter.UserConverter
import com.xiaolin.system.infra.mapper.UserAccountDao
import com.xiaolin.system.infra.persistence.UserAccount
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val userAccountDao: UserAccountDao): UserRepository {

    override fun create(user: UserAggregation): Boolean {
        val sysUser = UserConverter.aggregation2DO(userAggregation = user)
        return userAccountDao.insert(entity = sysUser) == 1
    }

    override fun getById(id: Long): UserAggregation {
        val user = userAccountDao.findById(id)
        require(user != null) { "用户不存在!" }
        return UserConverter.do2Aggregation(user)
    }

    override fun getByUsername(username: String): UserAggregation? {
        val user = userAccountDao.findByUsername(username)
        return user?.let { UserConverter.do2Aggregation(it) }
    }

    override fun list(): Page<UserAggregation> {
        val list: Page<UserAccount> = userAccountDao.queryPage(
            sql = "SELECT * FROM sys.user_account",
            countSql = "SELECT COUNT(1) FROM sys.user_account",
            params = MapSqlParameterSource(),
            pageable = PageRequest.of(0, 10)
        )
        return list.map { UserConverter.do2Aggregation(it) }
    }

    override fun update(user: UserAggregation): Boolean {
        val sysUser = UserConverter.aggregation2DO(userAggregation = user)
        return userAccountDao.update(entity = sysUser) == 1
    }

}