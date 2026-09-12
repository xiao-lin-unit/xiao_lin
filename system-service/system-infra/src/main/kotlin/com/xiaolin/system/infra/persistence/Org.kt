//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 组织：租户内部组织树，支撑 ORG_SELF / ORG_TREE 类数据范围 */
//data class Org(
//    val id: Long = 0,
//    val tenantId: Long,
//    val parentId: Long? = null,
//    /** 物化路径，如 /3/17/42/。由 DB 触发器维护，移动节点会级联修正子树 */
//    var path: String = "",
//    var levelNo: Int = 1,
//    val code: String? = null,
//    /** 村民小组 / 摊位区 / 门店 */
//    val name: String,
//    /** 组织负责人（用于 MEMBER_TEAM 范围） */
//    val leaderMemberId: Long? = null,
//    val sortNo: Int = 0,
//    val status: Int = 1, // smallint
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
