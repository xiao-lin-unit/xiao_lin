package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotBlank

class IdentityTypeCreateCommand(
    @field:NotBlank(message = "请输入身份类型编码")
    val code: String,

    @field:NotBlank(message = "请输入身份类型名称")
    val name: String,

    /** 绑定的租户类型，为空表示通用身份 */
    val tenantTypeId: Long? = null,

    val description: String? = null,

    val builtin: Boolean? = null,

    val sortNo: Int? = null,
)
