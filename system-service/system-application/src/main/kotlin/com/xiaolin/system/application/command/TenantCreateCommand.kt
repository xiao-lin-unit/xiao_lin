package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

class TenantCreateCommand(
    /** 租户类型 */
    @field:NotNull(message = "请选择租户类型")
    val tenantTypeId: Long?,

    /** 上级租户，预留：代理商 -> 下级代理 */
    val parentId: Long? = null,

    /** 租户唯一编码 */
    @field:NotBlank(message = "请输入租户编码")
    val code: String,

    @field:NotBlank(message = "请输入租户名称")
    val name: String,

    val shortName: String? = null,

    /** 行政区划码 */
    val regionCode: String? = null,

    val regionName: String? = null,

    val address: String? = null,

    val contactName: String? = null,

    val contactPhone: String? = null,

    /** 1正常 0待审核 2冻结 3注销 */
    val status: Int? = null,

    val expireAt: OffsetDateTime? = null,

    /** 租户个性化配置 JSON */
    val config: String? = null,
)
