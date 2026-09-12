package com.xiaolin.system.interfaces.vo

data class IdentityTypeVO(
    val id: Long? = null,
    val code: String,
    val name: String,
    /** NULL = 通用身份 */
    val tenantTypeId: Long? = null,
    val description: String? = null,
    val builtin: Boolean? = null,
    val sortNo: Int? = null,
)
