package com.xiaolin.shared.infra.persistence

import com.xiaolin.shared.infra.dao.SqlType
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import java.sql.Types
import java.time.OffsetDateTime

abstract class BaseEntity (
    @Id
    @Column("id")
    val id: Long,
    @CreatedDate
    @Column("created_at")
    @SqlType(Types.TIMESTAMP_WITH_TIMEZONE)
    open val createdAt: OffsetDateTime?,
    @LastModifiedDate
    @Column("updated_at")
    @SqlType(Types.TIMESTAMP_WITH_TIMEZONE)
    open val updatedAt: OffsetDateTime?,
    @CreatedBy
    @Column("created_by")
    open val createdBy: Long?,
    @LastModifiedBy
    @Column
    open val updatedBy: Long?,
    @Column("deleted_at")
    @SqlType(Types.TIMESTAMP_WITH_TIMEZONE)
    open val deletedAt: OffsetDateTime?,
    @Column("deleted")
    open val deleted: Boolean,
    @Column("deleted_by")
    open val deletedBy: Long?,
){
    constructor(): this(
        id = 0L,
        createdAt = null,
        updatedAt = null,
        createdBy = null,
        updatedBy = null,
        deletedAt = null,
        deleted = false,
        deletedBy = null
    )
}

abstract class BizBaseEntity(
    id: Long,
    createdAt: OffsetDateTime?,
    updatedAt: OffsetDateTime?,
    createdBy: Long?,
    updatedBy: Long?,
    deletedAt: OffsetDateTime?,
    deleted: Boolean,
    deletedBy: Long?,
    @Column("tenant_id")
    open val tenantId: Long = 0L
): BaseEntity(
    id = id,
    createdAt,
    updatedAt,
    createdBy,
    updatedBy,
    deletedAt,
    deleted,
    deletedBy
) {
    constructor(): this(
        id = 0L,
        createdAt = null,
        updatedAt = null,
        createdBy = null,
        updatedBy = null,
        deletedAt = null,
        deleted = false,
        deletedBy = null,
        tenantId = 0L
    )
}

