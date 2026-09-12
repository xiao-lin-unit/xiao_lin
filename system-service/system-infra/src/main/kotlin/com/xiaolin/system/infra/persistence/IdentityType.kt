package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

/** 身份类型 */
@Table("sys.identity_type")
class IdentityType(
    id: Long = 0,
    val code: String,
    val name: String,
    /** NULL = 通用身份 */
    val tenantTypeId: Long? = null,
    val description: String? = null,
    val builtin: Boolean = false,
    val sortNo: Int = 0,
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
