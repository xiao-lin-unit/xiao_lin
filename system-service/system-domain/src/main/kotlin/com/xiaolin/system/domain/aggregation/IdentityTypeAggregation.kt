package com.xiaolin.system.domain.aggregation

import java.time.Clock
import java.time.OffsetDateTime

/**
 * 身份类型聚合根
 *
 * 农户 / 商贩 / 化肥农药商贩 等身份。
 * tenantTypeId 为 null 表示"通用身份"，任何租户类型都可用；
 * 有值则表示该身份只在特定租户类型下生效。
 */
class IdentityTypeAggregation(
    val id: Long,
    var code: String,
    var name: String,
    /** NULL = 通用身份 */
    var tenantTypeId: Long? = null,
    var description: String? = null,
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

    class IdentityTypeAggregationBuilder {
        var id: Long = 0L
        lateinit var code: String
        lateinit var name: String
        var tenantTypeId: Long? = null
        var description: String? = null
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
        fun tenantTypeId(tenantTypeId: Long?) = apply { this.tenantTypeId = tenantTypeId }
        fun description(description: String?) = apply { this.description = description }
        fun builtin(builtin: Boolean?) = apply { this.builtin = builtin ?: false }
        fun sortNo(sortNo: Int?) = apply { this.sortNo = sortNo ?: 0 }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): IdentityTypeAggregation = IdentityTypeAggregation(
            id = id,
            code = code,
            name = name,
            tenantTypeId = tenantTypeId,
            description = description,
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
        fun create(id: Long): IdentityTypeAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): IdentityTypeAggregationBuilder = IdentityTypeAggregationBuilder().id(id)
    }

    fun isDeleted(): Boolean = deleted

    /** 是否通用身份（不绑定任何租户类型） */
    fun isGlobal(): Boolean = tenantTypeId == null

    fun del(operatorId: Long?) = apply {
        val now = OffsetDateTime.now(Clock.systemUTC())
        this.deleted = true
        this.deletedAt = now
        this.deletedBy = operatorId
        this.updatedAt = now
        this.updatedBy = operatorId
    }

    fun verifyDeletable() {
        require(!builtin) { "内置身份类型不可删除: $code" }
        require(!deleted) { "身份类型已删除: $code" }
    }

    fun verify() {
        require(code.isNotBlank()) { "身份类型编码不能为空" }
        require(name.isNotBlank()) { "身份类型名称不能为空" }
    }
}
