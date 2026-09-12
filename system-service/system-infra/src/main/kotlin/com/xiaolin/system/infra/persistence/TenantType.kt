package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.infra.dao.SqlType
import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.sql.Types
import java.time.OffsetDateTime

/** 租户类型 */
@Table("sys.tenant_type")
class TenantType(
    id: Long = 0,
    val code: String,
    val name: String,
    val description: String? = null,
    /** 组织层级模板，创建租户时据此生成默认组织树。例：[{"code":"VILLAGE","name":"村"}] */
    @SqlType(Types.OTHER)
    val orgLevels: String = "[]",
    /** 默认启用的应用 code 列表，创建租户时自动开通。权威来源是 sys.tenant_type_app */
    @SqlType(Types.OTHER)
    val defaultApps: String = "[]",
    /** 1启用 0停用 */
    val status: Int = 1, // smallint
    /** 内置类型不可删除 */
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


/** 1.3 租户类型 ↔ 应用（决定哪类租户能看到哪些应用） */
data class TenantTypeApp(
    val id: Long = 0,
    val tenantTypeId: Long,
    val appId: Long,
    /** 登录后默认进入 */
    val isDefaultApp: Boolean = false,
    /** 强制开通，不可关闭 */
    val isRequired: Boolean = false,
    val sortNo: Int = 0,
    val createdAt: OffsetDateTime,
)
