package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.CommonStatus
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 角色聚合根（租户内、按应用划分）
 *
 * parentId 支持角色继承（RBAC1）：子角色自动拥有父角色的权限。
 * 权限快照计算时展开角色继承树，详见 sys.member_role / sys.role_permission。
 */
class RoleAggregation(
    val id: Long,
    val tenantId: Long,
    val appId: Long,
    /** 角色继承 */
    var parentId: Long? = null,
    var code: String,
    var name: String,
    var description: String? = null,
    /** 租户管理员角色：权限自动跟随应用 */
    var isTenantAdmin: Boolean = false,
    /** 系统预置，不可删除 */
    var builtin: Boolean = false,
    /** 1启用 0停用 */
    var status: Int = CommonStatus.ENABLED,
    var sortNo: Int = 0,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    var createdBy: Long? = null,
    var updatedBy: Long? = null,
    var deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class RoleAggregationBuilder {
        var id: Long = 0L
        var tenantId: Long = 0L
        var appId: Long = 0L
        var parentId: Long? = null
        lateinit var code: String
        lateinit var name: String
        var description: String? = null
        var isTenantAdmin: Boolean = false
        var builtin: Boolean = false
        var status: Int = CommonStatus.ENABLED
        var sortNo: Int = 0
        var createdAt: OffsetDateTime? = null
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun tenantId(tenantId: Long) = apply { this.tenantId = tenantId }
        fun appId(appId: Long) = apply { this.appId = appId }
        fun parentId(parentId: Long?) = apply { this.parentId = parentId }
        fun code(code: String) = apply { this.code = code }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String?) = apply { this.description = description }
        fun isTenantAdmin(isTenantAdmin: Boolean?) = apply { this.isTenantAdmin = isTenantAdmin ?: false }
        fun builtin(builtin: Boolean?) = apply { this.builtin = builtin ?: false }
        fun status(status: Int?) = apply { this.status = status ?: CommonStatus.ENABLED }
        fun sortNo(sortNo: Int?) = apply { this.sortNo = sortNo ?: 0 }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): RoleAggregation = RoleAggregation(
            id = id,
            tenantId = tenantId,
            appId = appId,
            parentId = parentId,
            code = code,
            name = name,
            description = description,
            isTenantAdmin = isTenantAdmin,
            builtin = builtin,
            status = status,
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
        fun create(id: Long): RoleAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): RoleAggregationBuilder = RoleAggregationBuilder().id(id)
    }

    fun enable() = apply { status = CommonStatus.ENABLED }

    fun disable() = apply { status = CommonStatus.DISABLED }

    fun isDeleted(): Boolean = deleted

    /** 是否为根角色（无父角色） */
    fun isRoot(): Boolean = parentId == null

    fun del(operatorId: Long?) = apply {
        val now = OffsetDateTime.now(Clock.systemUTC())
        this.deleted = true
        this.deletedAt = now
        this.deletedBy = operatorId
        this.updatedAt = now
        this.updatedBy = operatorId
    }

    /**
     * 继承关系校验：禁止把父角色改成自己，避免形成自环。
     * 更深的环（A->B->A）需要在 Service 层沿链上溯检测，这里只挡最直接的一种。
     */
    fun changeParent(newParentId: Long?) = apply {
        require(newParentId != id) { "角色不能继承自己" }
        this.parentId = newParentId
        this.updatedAt = OffsetDateTime.now(Clock.systemUTC())
    }

    fun verifyDeletable(childCount: Long) {
        require(!builtin) { "系统预置角色不可删除: $code" }
        require(childCount == 0L) { "角色下仍有 $childCount 个子角色，请先删除或迁移" }
        require(!deleted) { "角色已删除: $code" }
    }

    fun verify() {
        require(tenantId > 0) { "角色所属租户不能为空" }
        require(appId > 0) { "角色所属应用不能为空" }
        require(code.isNotBlank()) { "角色编码不能为空" }
        require(name.isNotBlank()) { "角色名称不能为空" }
        require(parentId != id) { "角色不能继承自己" }
    }
}
