package com.xiaolin.system.domain.repository

import com.xiaolin.system.domain.aggregation.UserAggregation
import org.springframework.data.domain.Page

interface UserRepository {

    fun create(user: UserAggregation): Boolean

    fun getById(id: Long): UserAggregation

    fun getByUsername(username: String): UserAggregation?

    fun list(): Page<UserAggregation>

    fun update(user: UserAggregation): Boolean


}