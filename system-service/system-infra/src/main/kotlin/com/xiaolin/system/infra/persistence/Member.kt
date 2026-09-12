package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.infra.dao.SqlType
import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.sql.Types
import java.time.OffsetDateTime

/** 成员：用户在某租户内的身份，权限授予的最小载体。切租户 = 切 member */
@Table("sys.member")
class Member(
    id: Long = 0,
    val tenantId: Long,
    val userId: Long,
    /** 租户内成员编号 */
    val memberNo: String? = null,
    /** 在租户内展示的名称（可与真实姓名不同） */
    val displayName: String? = null,
    /** 主组织（村民小组 / 摊位区） */
    val orgId: Long? = null,
    /** 租户管理员：拥有全部应用权限 */
    val isTenantAdmin: Boolean = false,
    /** 1正常 0待审核 2停用 3退出 */
    val status: Int = 1, // smallint
    val joinedAt: OffsetDateTime,
    /** 扩展信息（摊位号、经营品类、资质等） */
    @SqlType(Types.OTHER)
    val profile: String = "{}",
    val invitedBy: Long? = null,
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


/** 成员 <-> 角色；权限快照计算时展开角色继承树 */
data class MemberRole(
    val id: Long = 0,
    val memberId: Long,
    val roleId: Long,
    val effectiveFrom: OffsetDateTime,
    /** NULL = 长期有效 */
    val effectiveTo: OffsetDateTime? = null,
    val grantedBy: Long? = null,
    val createdAt: OffsetDateTime,
)

/** 成员 <-> 组织（可跨组织兼任），支撑按组织下钻的数据范围 */
data class MemberOrg(
    val id: Long = 0,
    val memberId: Long,
    val orgId: Long,
    val isPrimary: Boolean = false,
    val createdAt: OffsetDateTime,
)

/** 成员身份：支持同一租户内多重身份 */
@Table("sys.member_identity")
class MemberIdentity(
    id: Long = 0,
    val memberId: Long,
    val identityTypeId: Long,
    /** 主身份 */
    val isPrimary: Boolean = false,
    val status: Int = 1, // smallint
    /** 实名 / 资质认证时间 */
    val verifiedAt: OffsetDateTime? = null,
    val verifiedBy: Long? = null,
    /** 资质材料（营业执照、摊位证等） */
    @SqlType(Types.OTHER)
    val credential: String = "{}",
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
