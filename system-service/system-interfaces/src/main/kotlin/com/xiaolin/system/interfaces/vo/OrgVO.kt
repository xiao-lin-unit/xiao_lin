package com.xiaolin.system.interfaces.vo

data class OrgVO(
    val id: Long? = null,
    val tenantId: Long? = null,
    val parentId: Long? = null,
    val path: String? = null,
    val levelNo: Int? = null,
    val code: String? = null,
    val name: String,
    val leaderMemberId: Long? = null,
    val sortNo: Int? = null,
    /** 1启用 0停用 */
    val status: Int? = null,
)
