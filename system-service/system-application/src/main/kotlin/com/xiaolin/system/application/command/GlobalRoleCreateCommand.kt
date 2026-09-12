package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotBlank

class GlobalRoleCreateCommand(
    @field:NotBlank(message = "请输入全局角色编码")
    val code: String,

    @field:NotBlank(message = "请输入全局角色名称")
    val name: String,

    val description: String? = null,

    /** 超管角色：绕过全部校验 */
    val isSuper: Boolean? = null,

    val builtin: Boolean? = null,

    /** 1启用 0停用 */
    val status: Int? = null,
)
