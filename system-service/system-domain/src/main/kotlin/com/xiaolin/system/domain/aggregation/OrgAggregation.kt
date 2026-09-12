package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.CommonStatus
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 组织聚合根
 *
 * 租户内部的组织树（村民小组 / 摊位区 / 门店），支撑 ORG_SELF / ORG_TREE 类数据范围。
 * path 与 level_no 由 DB 触发器 tg_cascade_org_path 维护，移动节点会级联修正子树。
 */
class OrgAggregation(
    val id: Long,
    val tenantId: Long,
    var parentId: Long? = null,
    /** 物化路径 /3/17/42/ */
    var path: String = "",
    var levelNo: Int = 1,
    var code: String? = null,
    /** 村民小组 / 摊位区 / 门店 */
    var name: String,
    /** 组织负责人（用于 MEMBER_TEAM 范围） */
    var leaderMemberId: Long? = null,
    var sortNo: Int = 0,
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

    class OrgAggregationBuilder {
        var id: Long = 0L
        var tenantId: Long = 0L
        var parentId: Long? = null
        var path: String = ""
        var levelNo: Int = 1
        var code: String? = null
        lateinit var name: String
        var leaderMemberId: Long? = null
        var sortNo: Int = 0
        var status: Int = CommonStatus.ENABLED
        var createdAt: OffsetDateTime? = null
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun tenantId(tenantId: Long) = apply { this.tenantId = tenantId }
        fun parentId(parentId: Long?) = apply { this.parentId = parentId }
        fun path(path: String?) = apply { this.path = path ?: "" }
        fun levelNo(levelNo: Int?) = apply { this.levelNo = levelNo ?: 1 }
        fun code(code: String?) = apply { this.code = code }
        fun name(name: String) = apply { this.name = name }
        fun leaderMemberId(leaderMemberId: Long?) = apply { this.leaderMemberId = leaderMemberId }
        fun sortNo(sortNo: Int?) = apply { this.sortNo = sortNo ?: 0 }
        fun status(status: Int?) = apply { this.status = status ?: CommonStatus.ENABLED }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): OrgAggregation = OrgAggregation(
            id = id,
            tenantId = tenantId,
            parentId = parentId,
            path = path,
            levelNo = levelNo,
            code = code,
            name = name,
            leaderMemberId = leaderMemberId,
            sortNo = sortNo,
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
        fun create(id: Long): OrgAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): OrgAggregationBuilder = OrgAggregationBuilder().id(id)
    }

    fun enable() = apply { status = CommonStatus.ENABLED }

    fun disable() = apply { status = CommonStatus.DISABLED }

    fun isDeleted(): Boolean = deleted

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
     * 移动节点：只改 parentId，path / levelNo 由 DB 触发器级联修正。
     * 应用层不自己拼 path —— 并发下拼出来的值会与触发器结果打架。
     */
    fun moveTo(newParentId: Long?) = apply {
        require(newParentId != id) { "组织不能挂到自己下面" }
        this.parentId = newParentId
        this.updatedAt = OffsetDateTime.now(Clock.systemUTC())
    }

    fun verify() {
        require(tenantId > 0) { "组织所属租户不能为空" }
        require(name.isNotBlank()) { "组织名称不能为空" }
        require(parentId != id) { "组织不能挂到自己下面" }
    }

    /** 删除前校验：仍有子节点时不许删，否则会留下孤儿节点 */
    fun verifyDeletable(childCount: Long) {
        require(childCount == 0L) { "组织下仍有 $childCount 个子组织，请先删除或迁移" }
        require(!deleted) { "组织已删除: $name" }
    }
}
