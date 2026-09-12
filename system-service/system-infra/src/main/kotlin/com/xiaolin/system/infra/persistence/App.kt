package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

/** 应用：功能权限的聚合单元，一个应用 = 一整套权限 */
@Table("sys.app")
class App(
    id: Long = 0,
    val code: String,
    val name: String,
    /** BUSINESS 业务 / ADMIN 管理 / PORTAL 门户 */
    val type: String = "BUSINESS",
    /** 前端入口 */
    val entryUrl: String? = null,
    val icon: String? = null,
    val description: String? = null,
    /** 1-启用 0-禁用 */
    val status: Int = 1, // smallint
    /** 是否内置 */
    val builtin: Boolean = false,
    /** 排序编号 */
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
