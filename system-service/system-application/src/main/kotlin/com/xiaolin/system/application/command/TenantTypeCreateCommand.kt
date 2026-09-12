package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotBlank

class TenantTypeCreateCommand(
    /** 租户类型编码，全局唯一 */
    @field:NotBlank(message = "请输入租户类型编码")
    val code: String,

    @field:NotBlank(message = "请输入租户类型名称")
    val name: String,

    val description: String? = null,

    /** 组织层级模板 JSON，如 [{"code":"VILLAGE","name":"村"}] */
    val orgLevels: String? = null,

    /** 默认启用应用 code 列表 JSON */
    val defaultApps: String? = null,

    /** 1启用 0停用 */
    val status: Int? = null,

    val builtin: Boolean? = null,

    val sortNo: Int? = null,
)
