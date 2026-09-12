//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 角色：租户内、按应用划分，parentId 支持角色继承（RBAC1） */
//data class Role(
//    val id: Long = 0,
//    val tenantId: Long,
//    val appId: Long,
//    /** 角色继承 */
//    val parentId: Long? = null,
//    val code: String,
//    val name: String,
//    val description: String? = null,
//    /** 租户管理员角色：权限自动跟随应用 */
//    val isTenantAdmin: Boolean = false,
//    /** 系统预置，不可删除 */
//    val builtin: Boolean = false,
//    val status: Int = 1, // smallint
//    val sortNo: Int = 0,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
//
//
///** 全局角色：不属于任何租户，用于超级管理员及未来的运营角色 */
//data class GlobalRole(
//    val id: Long = 0,
//    val code: String,
//    val name: String,
//    val description: String? = null,
//    /** 超管角色：绕过全部校验 */
//    val isSuper: Boolean = false,
//    val builtin: Boolean = false,
//    val status: Int = 1, // smallint
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
//
///** 角色 <-> 功能权限（仅限该角色所属应用下的权限） */
//data class RolePermission(
//    val id: Long = 0,
//    val roleId: Long,
//    val permissionId: Long,
//    val createdAt: OffsetDateTime,
//)
//
///** 全局角色 <-> 功能权限 */
//data class GlobalRolePermission(
//    val id: Long = 0,
//    val globalRoleId: Long,
//    val permissionId: Long,
//    val createdAt: OffsetDateTime,
//)
