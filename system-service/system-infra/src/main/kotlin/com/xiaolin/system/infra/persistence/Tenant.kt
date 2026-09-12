package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

/** 租户：数据隔离的基本单位 */
@Table("sys.tenant")
class Tenant(
    id: Long = 0,
    val tenantTypeId: Long,
    /** 预留：代理商 → 下级代理 */
    val parentId: Long? = null,
    /** 租户树路径，如 /1/12/135/。由 DB 触发器维护，移动节点会级联修正子树 */
    var path: String = "/",
    var levelNo: Int = 1,
    /** 租户唯一编码 */
    val code: String,
    val name: String,
    val shortName: String? = null,
    /** 行政区划码，便于按地区运营 */
    val regionCode: String? = null,
    val regionName: String? = null,
    val address: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    /** 1正常 0待审核 2冻结 3注销 */
    val status: Int = 1, // smallint
    val expireAt: OffsetDateTime? = null,
    /** 租户个性化配置（菜单、参数、开关） */
    val config: String = "{}",
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime? = null,
    deletedAt: OffsetDateTime? = null,
    createdBy: Long? = null,
    updatedBy: Long? = null,
    deletedBy: Long? = null,
    deleted: Boolean = false,
) : BaseEntity(
    id = id,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    createdBy = createdBy,
    updatedBy = updatedBy,
    deletedBy = deletedBy,
    deleted = deleted,
)
