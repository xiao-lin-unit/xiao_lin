package com.xiaolin.system.infra.persistence//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 登录日志（按月 RANGE 分区） */
//data class LoginLog(
//    val id: Long = 0,
//    val userId: Long? = null,
//    val phone: String? = null,
//    /** PASSWORD / SMS / WECHAT */
//    val loginType: String? = null,
//    /** 1成功 0失败 */
//    val result: Int, // smallint
//    val failReason: String? = null,
//    val ip: String? = null, // inet
//    val userAgent: String? = null,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//): BaseEntity
//
///** 审计日志：权限相关操作全量留痕，按月分区，保留 3 年 */
//data class AuditLog(
//    val id: Long = 0,
//    val operatorUserId: Long? = null,
//    val operatorMemberId: Long? = null,
//    val tenantId: Long? = null,
//    val appId: Long? = null,
//    /** ROLE / PERMISSION / DATASCOPE / MEMBER / TENANT */
//    val module: String,
//    /** GRANT_ROLE / REVOKE_ROLE / BIND_SCOPE / SWITCH_TENANT ... */
//    val action: String,
//    val targetType: String? = null,
//    val targetId: Long? = null,
//    val beforeValue: String? = null,
//    val afterValue: String? = null,
//    val ip: String? = null, // inet
//    val remark: String? = null,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//): BaseEntity
//
