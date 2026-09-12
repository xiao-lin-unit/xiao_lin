package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.MemberStatus
import com.xiaolin.shared.common.provider.TimeProvider
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 成员聚合根
 *
 * 用户在某个租户内的身份，是权限授予的最小载体（切租户 = 切 member）。
 * 一个 sys.user_account 可以在多个租户下各有一个 member，支撑"既是商贩又是农户"。
 */
class MemberAggregation(
    val id: Long,
    val tenantId: Long,
    val userId: Long,
    /** 租户内成员编号 */
    var memberNo: String? = null,
    /** 在租户内展示的名称（可与真实姓名不同） */
    var displayName: String? = null,
    /** 主组织（村民小组 / 摊位区） */
    var orgId: Long? = null,
    /** 租户管理员：拥有全部应用权限 */
    var isTenantAdmin: Boolean = false,
    /** 1正常 0待审核 2停用 3退出 */
    var status: Int = MemberStatus.PENDING,
    var joinedAt: OffsetDateTime? = null,
    /** 扩展信息 JSON（摊位号、经营品类、资质等） */
    var profile: String = "{}",
    var invitedBy: Long? = null,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    var createdBy: Long? = null,
    var updatedBy: Long? = null,
    var deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class MemberAggregationBuilder {
        var id: Long = 0L
        var tenantId: Long = 0L
        var userId: Long = 0L
        var memberNo: String? = null
        var displayName: String? = null
        var orgId: Long? = null
        var isTenantAdmin: Boolean = false
        var status: Int = MemberStatus.PENDING
        var joinedAt: OffsetDateTime? = null
        var profile: String = "{}"
        var invitedBy: Long? = null
        var createdAt: OffsetDateTime? = null
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun tenantId(tenantId: Long) = apply { this.tenantId = tenantId }
        fun userId(userId: Long) = apply { this.userId = userId }
        fun memberNo(memberNo: String?) = apply { this.memberNo = memberNo }
        fun displayName(displayName: String?) = apply { this.displayName = displayName }
        fun orgId(orgId: Long?) = apply { this.orgId = orgId }
        fun isTenantAdmin(isTenantAdmin: Boolean?) = apply { this.isTenantAdmin = isTenantAdmin ?: false }
        fun status(status: Int?) = apply { this.status = status ?: MemberStatus.PENDING }
        fun joinedAt(joinedAt: OffsetDateTime?) = apply { this.joinedAt = joinedAt }
        fun profile(profile: String?) = apply { this.profile = profile ?: "{}" }
        fun invitedBy(invitedBy: Long?) = apply { this.invitedBy = invitedBy }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): MemberAggregation = MemberAggregation(
            id = id,
            tenantId = tenantId,
            userId = userId,
            memberNo = memberNo,
            displayName = displayName,
            orgId = orgId,
            isTenantAdmin = isTenantAdmin,
            status = status,
            joinedAt = joinedAt,
            profile = profile,
            invitedBy = invitedBy,
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
        fun create(id: Long): MemberAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .joinedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): MemberAggregationBuilder = MemberAggregationBuilder().id(id)
    }

    /** 审核通过 */
    fun approve() = apply { status = MemberStatus.NORMAL; touch() }

    /** 停用：保留成员记录与历史数据，但不可登录该租户 */
    fun disable() = apply { status = MemberStatus.DISABLED; touch() }

    fun enable() = apply { status = MemberStatus.NORMAL; touch() }

    /** 退出：区别于删除，退出是业务动作 */
    fun quit() = apply { status = MemberStatus.QUIT; touch() }

    fun grantTenantAdmin() = apply { isTenantAdmin = true; touch() }

    fun revokeTenantAdmin() = apply { isTenantAdmin = false; touch() }

    fun isDeleted(): Boolean = deleted

    fun isNormal(): Boolean = status == MemberStatus.NORMAL

    fun del(operatorId: Long?) = apply {
        val now = TimeProvider.now()
        this.deleted = true
        this.deletedAt = now
        this.deletedBy = operatorId
        this.updatedAt = now
        this.updatedBy = operatorId
    }

    private fun touch(now: OffsetDateTime = TimeProvider.now()) = apply {
        this.updatedAt = now
    }

    /**
     * DDL 有 ck_member_status 约束：status IN (0,1,2,3)
     * 且 uk_member_tenant_user 保证同一租户下 user 唯一。
     */
    fun verify() {
        require(tenantId > 0) { "成员所属租户不能为空" }
        require(userId > 0) { "成员关联用户不能为空" }
        require(status in MemberStatus.PENDING..MemberStatus.QUIT) {
            "成员状态非法：$status（允许 0待审核 1正常 2停用 3退出）"
        }
    }
}
