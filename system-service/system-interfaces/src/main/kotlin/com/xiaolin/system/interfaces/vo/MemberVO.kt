package com.xiaolin.system.interfaces.vo

import java.time.OffsetDateTime

data class MemberVO(
    val id: Long? = null,
    val tenantId: Long? = null,
    val userId: Long? = null,
    val memberNo: String? = null,
    val displayName: String? = null,
    val orgId: Long? = null,
    val isTenantAdmin: Boolean? = null,
    /** 1正常 0待审核 2停用 3退出 */
    val status: Int? = null,
    val joinedAt: OffsetDateTime? = null,
    val invitedBy: Long? = null,
)
