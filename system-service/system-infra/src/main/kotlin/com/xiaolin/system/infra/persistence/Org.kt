package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

/** 组织：租户内部组织树，支撑 ORG_SELF / ORG_TREE 类数据范围 */
@Table("sys.org")
class Org(
    id: Long = 0,
    val tenantId: Long,
    val parentId: Long? = null,
    /** 物化路径，如 /3/17/42/。由 DB 触发器维护，移动节点会级联修正子树 */
    var path: String = "",
    var levelNo: Int = 1,
    val code: String? = null,
    /** 村民小组 / 摊位区 / 门店 */
    val name: String,
    /** 组织负责人（用于 MEMBER_TEAM 范围） */
    val leaderMemberId: Long? = null,
    val sortNo: Int = 0,
    val status: Int = 1, // smallint
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
