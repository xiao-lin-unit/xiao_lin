//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 应用：功能权限的聚合单元，一个应用 = 一整套权限 */
//data class App(
//    val id: Long = 0,
//    val code: String,
//    val name: String,
//    /** BUSINESS 业务 / ADMIN 管理 / PORTAL 门户 */
//    val type: String = "BUSINESS",
//    /** 前端入口 */
//    val entryUrl: String? = null,
//    val icon: String? = null,
//    val description: String? = null,
//    /** 1-启用 0-禁用 */
//    val status: Int = 1, // smallint
//    /** 是否内置 */
//    val builtin: Boolean = false,
//    /** 排序编号 */
//    val sortNo: Int = 0,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
