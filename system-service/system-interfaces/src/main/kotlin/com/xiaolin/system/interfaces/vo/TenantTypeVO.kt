package com.xiaolin.system.interfaces.vo

import java.time.OffsetDateTime

data class TenantTypeVO(
    val id: Long? = null,
    val code: String,
    val name: String,
    val description: String? = null,
    /** 组织层级模板 JSON */
    val orgLevels: String? = null,
    /** 默认启用应用 code 列表 JSON */
    val defaultApps: String? = null,
    /** 1启用 0停用 */
    val status: Int? = null,
    val builtin: Boolean? = null,
    val sortNo: Int? = null,
    val createdAt: OffsetDateTime? = null,
)
