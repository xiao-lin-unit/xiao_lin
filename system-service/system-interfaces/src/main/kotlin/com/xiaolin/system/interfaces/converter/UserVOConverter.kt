package com.xiaolin.system.interfaces.converter

import com.xiaolin.system.domain.aggregation.UserAggregation
import com.xiaolin.system.interfaces.vo.UserVO

object UserVOConverter {

    fun toUserVO(userAggregation: UserAggregation): UserVO {
        return UserVO(
            id = userAggregation.id,
            username = userAggregation.username,
            email = userAggregation.email,
            phone = userAggregation.phone,
            realName = userAggregation.realName,
            avatar = userAggregation.avatar,
            gender = userAggregation.gender,
            userKind = userAggregation.userKind,
            status = userAggregation.status
        )
    }

}