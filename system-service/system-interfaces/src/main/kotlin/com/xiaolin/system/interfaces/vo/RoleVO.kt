package com.xiaolin.system.interfaces.vo

data class RoleVO(
    val id: Long? = null,
    val tenantId: Long? = null,
    val appId: Long? = null,
    val parentId: Long? = null,
    val code: String,
    val name: String,
    val description: String? = null,
    val isTenantAdmin: Boolean? = null,
    val builtin: Boolean? = null,
    /** 1启用 0停用 */
    val status: Int? = null,
    val sortNo: Int? = null,
)
