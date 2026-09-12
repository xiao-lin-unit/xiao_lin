package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.CommonStatus
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 租户类型聚合根
 *
 * 系统级数据：决定"一类租户"能使用哪些应用、默认组织树长什么样。
 * 例：村 / 农贸市场 / 化肥农药厂或代理商。
 */
class TenantTypeAggregation(
    val id: Long,
    var code: String,
    var name: String,
    var description: String? = null,
    /** 组织层级模板 JSON，如 [{"code":"VILLAGE","name":"村"}] */
    var orgLevels: String = "[]",
    /** 默认启用应用 code 列表 JSON；权威来源是 sys.tenant_type_app，本列仅作创建租户时的快捷默认值 */
    var defaultApps: String = "[]",
    /** 1启用 0停用 */
    var status: Int = CommonStatus.ENABLED,
    /** 内置类型不可删除 */
    var builtin: Boolean = false,
    var sortNo: Int = 0,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    var createdBy: Long? = null,
    var updatedBy: Long? = null,
    var deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class TenantTypeAggregationBuilder {
        var id: Long = 0L
        lateinit var code: String
        lateinit var name: String
        var description: String? = null
        var orgLevels: String = "[]"
        var defaultApps: String = "[]"
        var status: Int = CommonStatus.ENABLED
        var builtin: Boolean = false
        var sortNo: Int = 0
        var createdAt: OffsetDateTime? = null
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun code(code: String) = apply { this.code = code }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String?) = apply { this.description = description }
        fun orgLevels(orgLevels: String?) = apply { this.orgLevels = orgLevels ?: "[]" }
        fun defaultApps(defaultApps: String?) = apply { this.defaultApps = defaultApps ?: "[]" }
        fun status(status: Int?) = apply { this.status = status ?: CommonStatus.ENABLED }
        fun builtin(builtin: Boolean?) = apply { this.builtin = builtin ?: false }
        fun sortNo(sortNo: Int?) = apply { this.sortNo = sortNo ?: 0 }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): TenantTypeAggregation = TenantTypeAggregation(
            id = id,
            code = code,
            name = name,
            description = description,
            orgLevels = orgLevels,
            defaultApps = defaultApps,
            status = status,
            builtin = builtin,
            sortNo = sortNo,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            createdBy = createdBy,
            updatedBy = updatedBy,
            deletedBy = deletedBy,
            deleted = deleted,
        )
    }

    companion object {
        fun create(id: Long): TenantTypeAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): TenantTypeAggregationBuilder = TenantTypeAggregationBuilder().id(id)
    }

    fun enable() = apply { status = CommonStatus.ENABLED }

    fun disable() = apply { status = CommonStatus.DISABLED }

    fun isDeleted(): Boolean = deleted

    fun isBuiltin(): Boolean = builtin

    fun del(operatorId: Long?) = apply {
        val now = OffsetDateTime.now(Clock.systemUTC())
        this.deleted = true
        this.deletedAt = now
        this.deletedBy = operatorId
        this.updatedAt = now
        this.updatedBy = operatorId
    }

    /**
     * 内置类型禁止删除 —— 删除内置类型会让既有租户失去类型归属
     */
    fun verifyDeletable() {
        require(!builtin) { "内置租户类型不可删除: $code" }
        require(!deleted) { "租户类型已删除: $code" }
    }

    /**
     * code 全局唯一，创建时必须校验
     */
    fun verify() {
        require(code.isNotBlank()) { "租户类型编码不能为空" }
        require(name.isNotBlank()) { "租户类型名称不能为空" }
    }
}
