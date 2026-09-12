package com.xiaolin.system.application.command

import com.xiaolin.shared.common.constants.PermissionType
import jakarta.validation.constraints.NotNull

class PermissionCreateCommand (
    @field:NotNull(message = "应用不能为空")
    val appId: Long?,
    @field:NotNull(message = "父权限不能为空")
    val parentId: Long = 0L,
    @field:NotNull(message = "权限码不能为空")
    val code: String,
    @field:NotNull(message = "权限名不能为空")
    val name: String,
    /** MENU 菜单 / BUTTON 按钮 / API 接口 / ELEMENT 界面元素 */
    @field:NotNull(message = "权限类型不能为空")
    val type: PermissionType = PermissionType.API,
    val apiMethod: String? = null,
    /** Ant 通配，如 /api/trade/orders/**/ */
    val apiPattern: String? = null,
    /** 前端组件路径（MENU 用） */
    val componentPath: String? = null,
    val icon: String? = null,
    val sortNo: Int = 0,
    /** 1-启用 0-禁用 */
    val status: Int? = null,
    /** 系统内置，租户不可增删改 */
    val builtin: Boolean? = null,
    val remark: String? = null,
)