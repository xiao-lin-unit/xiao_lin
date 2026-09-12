package com.xiaolin.system.infra.persistence//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 用户当前上下文（刷新页面 / 换设备可恢复；主副本在 Redis，此表用于持久化恢复） */
//data class UserContext(
//    val userId: Long,
//    val currentMemberId: Long,
//    val currentTenantId: Long,
//    val currentAppId: Long,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
