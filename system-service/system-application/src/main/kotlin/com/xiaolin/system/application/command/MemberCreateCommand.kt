package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotNull

class MemberCreateCommand(
    @field:NotNull(message = "成员所属租户不能为空")
    val tenantId: Long?,

    @field:NotNull(message = "成员关联用户不能为空")
    val userId: Long?,

    /** 租户内成员编号 */
    val memberNo: String? = null,

    /** 在租户内展示的名称，可与真实姓名不同 */
    val displayName: String? = null,

    /** 主组织（村民小组 / 摊位区） */
    val orgId: Long? = null,

    /** 租户管理员：拥有全部应用权限 */
    val isTenantAdmin: Boolean? = null,

    /** 1正常 0待审核 2停用 3退出 */
    val status: Int? = null,

    /** 扩展信息 JSON（摊位号、经营品类、资质等） */
    val profile: String? = null,

    val invitedBy: Long? = null,
)
