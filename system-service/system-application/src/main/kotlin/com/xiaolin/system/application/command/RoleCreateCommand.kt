package com.xiaolin.system.application.command

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class RoleCreateCommand(
    @field:NotNull(message = "角色所属租户不能为空")
    val tenantId: Long?,

    @field:NotNull(message = "角色所属应用不能为空")
    val appId: Long?,

    /** 父角色，支持角色继承（RBAC1） */
    val parentId: Long? = null,

    @field:NotBlank(message = "请输入角色编码")
    val code: String,

    @field:NotBlank(message = "请输入角色名称")
    val name: String,

    val description: String? = null,

    /** 租户管理员角色：权限自动跟随应用 */
    val isTenantAdmin: Boolean? = null,

    val builtin: Boolean? = null,

    /** 1启用 0停用 */
    val status: Int? = null,

    val sortNo: Int? = null,
)
