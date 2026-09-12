package com.xiaolin.system.application.command

import com.xiaolin.shared.common.constants.AppType
import jakarta.validation.constraints.NotBlank

class AppCreateCommand(
    /** 应用编码，全局唯一 */
    @field:NotBlank(message = "请输入应用编码")
    val code: String,

    @field:NotBlank(message = "请输入应用名称")
    val name: String,

    /** BUSINESS 业务 / ADMIN 管理 / PORTAL 门户 */
    @field:NotBlank(message = "请输入应用类型")
    val type: AppType = AppType.BUSINESS,

    /** 前端入口 */
    val entryUrl: String? = null,

    val icon: String? = null,

    val description: String? = null,

    /** 1启用 0停用 */
    val status: Int? = null,

    val builtin: Boolean? = null,

    val sortNo: Int? = null,
)
