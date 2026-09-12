package com.xiaolin.system.infra.persistence

import com.xiaolin.shared.common.constants.PermissionType
import com.xiaolin.shared.infra.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table("sys.permission")
/** 功能权限：系统级数据，租户只读 */
class Permission(
    id: Long,
    val appId: Long,
    val parentId: Long = 0L,
    /** 如 trade:order:create */
    val code: String,
    val name: String,
    /** MENU 菜单 / BUTTON 按钮 / API 接口 / ELEMENT 界面元素 */
    val type: PermissionType = PermissionType.API,
    /** GET / POST / PUT / DELETE / * */
    val apiMethod: String? = null,
    /** Ant 通配，如 /api/trade/orders/**/ */
    val apiPattern: String? = null,
    /** 前端组件路径（MENU 用） */
    val componentPath: String? = null,
    val icon: String? = null,
    val sortNo: Int = 0,
    /** 1-启用 0-禁用 */
    val status: Int = 1,
    /** 系统内置，租户不可增删改 */
    val builtin: Boolean = true,
    val remark: String? = null,
    createdAt: OffsetDateTime? = null,
    updatedAt: OffsetDateTime? = null,
    deletedAt: OffsetDateTime? = null,
    createdBy: Long? = null,
    updatedBy: Long? = null,
    deletedBy: Long? = null,
    deleted: Boolean = false,
) : BaseEntity(id, createdAt, updatedAt, createdBy, updatedBy, deletedAt, deleted, deletedBy)
