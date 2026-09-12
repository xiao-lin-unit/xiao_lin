package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.PermissionType
import java.time.Clock
import java.time.OffsetDateTime

class PermissionAggregation(
    val id: Long,
    var appId: Long,
    var parentId: Long = 0L,
    /** 如 trade:order:create */
    var code: String,
    var name: String,
    /** MENU 菜单 / BUTTON 按钮 / API 接口 / ELEMENT 界面元素 */
    var type: PermissionType = PermissionType.API,
    /** GET / POST / PUT / DELETE / * */
    var apiMethod: String? = null,
    /** Ant 通配，如 /api/trade/orders/**/ */
    var apiPattern: String? = null,
    /** 前端组件路径（MENU 用） */
    var componentPath: String? = null,
    var icon: String? = null,
    var sortNo: Int = 0,
    /** 1-启用 0-禁用 */
    var status: Int = 1,
    /** 系统内置，租户不可增删改 */
    var builtin: Boolean = true,
    var remark: String? = null,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    var createdBy: Long? = null,
    var updatedBy: Long? = null,
    var deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class PermissionAggregationBuilder() {
        var id: Long = 0L
        var appId: Long = 0L
        var parentId: Long = 0L
        /** 如 trade:order:create */
        lateinit var code: String
        lateinit var name: String
        /** MENU 菜单 / BUTTON 按钮 / API 接口 / ELEMENT 界面元素 */
        var type: PermissionType = PermissionType.API
        /** GET / POST / PUT / DELETE / * */
        var apiMethod: String? = null
        /** Ant 通配，如 /api/trade/orders/**/ */
        var apiPattern: String? = null
        /** 前端组件路径（MENU 用） */
        var componentPath: String? = null
        var icon: String? = null
        var sortNo: Int = 0
        /** 1-启用 0-禁用 */
        var status: Int = 1
        /** 系统内置，租户不可增删改 */
        var builtin: Boolean = true
        var remark: String? = null
        lateinit var createdAt: OffsetDateTime
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun appId(appId: Long) = apply { this.appId = appId }
        fun parentId(parentId: Long) = apply { this.parentId = parentId }
        fun code(code: String) = apply { this.code = code }
        fun name(name: String) = apply { this.name = name }
        fun type(type: PermissionType) = apply { this.type = type }
        fun apiMethod(apiMethod: String?) = apply { this.apiMethod = apiMethod }
        fun apiPattern(apiPattern: String?) = apply { this.apiPattern = apiPattern }
        fun componentPath(componentPath: String?) = apply { this.componentPath = componentPath }
        fun icon(icon: String?) = apply { this.icon = icon }
        fun sortNo(sortNo: Int) = apply { this.sortNo = sortNo }
        fun status(status: Int) = apply { this.status = status }
        fun builtin(builtin: Boolean) = apply { this.builtin = builtin }
        fun remark(remark: String?) = apply { this.remark = remark }
        fun createdAt(createdAt: OffsetDateTime) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean) = apply { this.deleted = deleted }

        fun build(): PermissionAggregation {
            return PermissionAggregation(
                id = id,
                appId = appId,
                parentId = parentId,
                code = code,
                name = name,
                type = type,
                apiMethod = apiMethod,
                apiPattern = apiPattern,
                componentPath = componentPath,
                icon = icon,
                sortNo = sortNo,
                status = status,
                builtin = builtin,
                remark = remark,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
                createdBy = createdBy,
                updatedBy = updatedBy,
                deletedBy = deletedBy,
                deleted = deleted
            )
        }

    }

    companion object {
        fun create(id: Long): PermissionAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): PermissionAggregationBuilder {
            return PermissionAggregationBuilder().id(id)
        }

    }

    fun enable() = apply { status = 1 }

    fun disable() = apply { status = 0 }
    fun verify() {
        val r = when(this.type) {
            PermissionType.MENU -> this.componentPath != null
            PermissionType.API -> this.apiMethod != null && this.apiPattern != null
            else -> true
        }
        if (!r) {
            throw IllegalArgumentException("菜单类型必须填写组件路径，API 类型必须填写接口方法和接口路径")
        }
    }

}