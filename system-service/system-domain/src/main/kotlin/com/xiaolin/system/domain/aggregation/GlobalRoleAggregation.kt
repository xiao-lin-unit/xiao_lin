package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.CommonStatus
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 全局角色聚合根
 *
 * 不属于任何租户，用于超级管理员及未来的平台运营角色。
 * isSuper = true 时绕过全部校验 —— 这是设计描述里"注意超级管理员这个特殊用户"的落点。
 */
class GlobalRoleAggregation(
    val id: Long,
    var code: String,
    var name: String,
    var description: String? = null,
    /** 超管角色：绕过全部校验 */
    var isSuper: Boolean = false,
    var builtin: Boolean = false,
    /** 1启用 0停用 */
    var status: Int = CommonStatus.ENABLED,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    var createdBy: Long? = null,
    var updatedBy: Long? = null,
    var deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class GlobalRoleAggregationBuilder {
        var id: Long = 0L
        lateinit var code: String
        lateinit var name: String
        var description: String? = null
        var isSuper: Boolean = false
        var builtin: Boolean = false
        var status: Int = CommonStatus.ENABLED
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
        fun isSuper(isSuper: Boolean?) = apply { this.isSuper = isSuper ?: false }
        fun builtin(builtin: Boolean?) = apply { this.builtin = builtin ?: false }
        fun status(status: Int?) = apply { this.status = status ?: CommonStatus.ENABLED }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): GlobalRoleAggregation = GlobalRoleAggregation(
            id = id,
            code = code,
            name = name,
            description = description,
            isSuper = isSuper,
            builtin = builtin,
            status = status,
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
        fun create(id: Long): GlobalRoleAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): GlobalRoleAggregationBuilder = GlobalRoleAggregationBuilder().id(id)
    }

    fun enable() = apply { status = CommonStatus.ENABLED }

    fun disable() = apply { status = CommonStatus.DISABLED }

    fun isDeleted(): Boolean = deleted

    fun del(operatorId: Long?) = apply {
        val now = OffsetDateTime.now(Clock.systemUTC())
        this.deleted = true
        this.deletedAt = now
        this.deletedBy = operatorId
        this.updatedAt = now
        this.updatedBy = operatorId
    }

    fun verifyDeletable() {
        require(!builtin) { "内置全局角色不可删除: $code" }
        // 超管角色是系统的最后一道保险，删掉会导致无人能恢复权限体系
        require(!isSuper) { "超级管理员角色不可删除: $code" }
        require(!deleted) { "全局角色已删除: $code" }
    }

    fun verify() {
        require(code.isNotBlank()) { "全局角色编码不能为空" }
        require(name.isNotBlank()) { "全局角色名称不能为空" }
    }
}
