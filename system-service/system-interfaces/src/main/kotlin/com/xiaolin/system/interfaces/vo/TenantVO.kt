package com.xiaolin.system.interfaces.vo

import java.time.OffsetDateTime

data class TenantVO(
    val id: Long? = null,
    val tenantTypeId: Long? = null,
    val parentId: Long? = null,
    val path: String? = null,
    val levelNo: Int? = null,
    val code: String,
    val name: String,
    val shortName: String? = null,
    val regionCode: String? = null,
    val regionName: String? = null,
    val address: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    /** 1正常 0待审核 2冻结 3注销 */
    val status: Int? = null,
    val expireAt: OffsetDateTime? = null,
    val createdAt: OffsetDateTime? = null,
)
