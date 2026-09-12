package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class OrgCreateCommand(
    @field:NotNull(message = "组织所属租户不能为空")
    val tenantId: Long?,

    /** 上级组织，为空表示根节点 */
    val parentId: Long? = null,

    /** 组织编码，租户内唯一（uk_org_tenant_code） */
    val code: String? = null,

    /** 村民小组 / 摊位区 / 门店 */
    @field:NotBlank(message = "请输入组织名称")
    val name: String,

    /** 组织负责人，用于 MEMBER_TEAM 数据范围 */
    val leaderMemberId: Long? = null,

    val sortNo: Int? = null,

    /** 1启用 0停用 */
    val status: Int? = null,
)
