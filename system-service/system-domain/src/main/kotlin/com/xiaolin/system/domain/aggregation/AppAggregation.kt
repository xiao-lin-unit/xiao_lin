package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.CommonStatus
import com.xiaolin.shared.common.constants.AppType
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 应用聚合根
 *
 * 一个应用 = 一整套功能权限的集合体（sys.permission 以 app_id 归属）。
 * 例：商贩交易应用 / 化肥农药交易应用 / 运营管理后台。
 */
class AppAggregation(
    val id: Long,
    var code: String,
    var name: String,
    /** BUSINESS 业务 / ADMIN 管理 / PORTAL 门户 */
    var type: AppType = AppType.BUSINESS,
    /** 前端入口 */
    var entryUrl: String? = null,
    var icon: String? = null,
    var description: String? = null,
    /** 1启用 0停用 */
    var status: Int = CommonStatus.ENABLED,
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

    class AppAggregationBuilder {
        var id: Long = 0L
        lateinit var code: String
        lateinit var name: String
        var type: AppType = AppType.BUSINESS
        var entryUrl: String? = null
        var icon: String? = null
        var description: String? = null
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
        fun type(type: AppType?) = apply { this.type = type ?: AppType.BUSINESS }
        fun entryUrl(entryUrl: String?) = apply { this.entryUrl = entryUrl }
        fun icon(icon: String?) = apply { this.icon = icon }
        fun description(description: String?) = apply { this.description = description }
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

        fun build(): AppAggregation = AppAggregation(
            id = id,
            code = code,
            name = name,
            type = type,
            entryUrl = entryUrl,
            icon = icon,
            description = description,
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
        fun create(id: Long): AppAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): AppAggregationBuilder = AppAggregationBuilder().id(id)
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
        require(!builtin) { "内置应用不可删除: $code" }
        require(!deleted) { "应用已删除: $code" }
    }

    /**
     * DDL 有 ck_app_type 约束：type IN ('BUSINESS','ADMIN','PORTAL')
     */
    fun verify() {
        require(code.isNotBlank()) { "应用编码不能为空" }
        require(name.isNotBlank()) { "应用名称不能为空" }
        require(type in setOf(AppType.BUSINESS, AppType.ADMIN, AppType.PORTAL)) {
            "应用类型必须是 BUSINESS / ADMIN / PORTAL，当前值：$type"
        }
    }
}
