package com.xiaolin.system.interfaces.vo

import com.xiaolin.shared.common.constants.AppType
import java.time.OffsetDateTime

data class AppVO(
    val id: Long? = null,
    val code: String,
    val name: String,
    /** BUSINESS 业务 / ADMIN 管理 / PORTAL 门户 */
    val type: AppType? = null,
    val entryUrl: String? = null,
    val icon: String? = null,
    val description: String? = null,
    /** 1启用 0停用 */
    val status: Int? = null,
    val builtin: Boolean? = null,
    val sortNo: Int? = null,
    val createdAt: OffsetDateTime? = null,
)
