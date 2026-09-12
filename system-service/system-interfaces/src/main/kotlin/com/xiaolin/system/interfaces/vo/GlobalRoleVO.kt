package com.xiaolin.system.interfaces.vo

data class GlobalRoleVO(
    val id: Long? = null,
    val code: String,
    val name: String,
    val description: String? = null,
    /** 超管角色：绕过全部校验 */
    val isSuper: Boolean? = null,
    val builtin: Boolean? = null,
    /** 1启用 0停用 */
    val status: Int? = null,
)
