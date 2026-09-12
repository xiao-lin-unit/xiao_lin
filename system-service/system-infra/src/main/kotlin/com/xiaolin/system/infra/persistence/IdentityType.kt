//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 身份类型 */
//data class IdentityType(
//    val id: Long = 0,
//    val code: String,
//    val name: String,
//    /** NULL = 通用身份 */
//    val tenantTypeId: Long? = null,
//    val description: String? = null,
//    val builtin: Boolean = false,
//    val sortNo: Int = 0,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
