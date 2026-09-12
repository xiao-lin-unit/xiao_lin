//package com.xiaolin.system.infra.persistence
//
//import com.xiaolin.shared.infra.persistence.BaseEntity
//import java.time.OffsetDateTime
//
///** 受数据权限保护的资源（业务表）注册表；拦截器据此改写 SQL */
//data class DataObject(
//    val id: Long = 0,
//    /** 如 TRADE_ORDER */
//    val code: String,
//    val name: String,
//    val schemaName: String = "biz",
//    /** 如 trade_order */
//    val tableName: String,
//    val tenantColumn: String = "tenant_id",
//    /** 如 org_id */
//    val orgColumn: String? = null,
//    /** 如 created_member_id */
//    val ownerMemberColumn: String? = null,
//    /** 如 created_by */
//    val ownerUserColumn: String? = null,
//    val defaultScopeType: String = "TENANT_ALL",
//    /** 1-启用 0-禁用 */
//    val status: Int = 1,
//    val remark: String? = null,
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
//
///** 数据范围策略：系统级预置，租户只能在其中选用，不能自建新规则 */
//data class DataScopePolicy(
//    val id: Long = 0,
//    val objectId: Long,
//    val code: String,
//    val name: String,
//    /** GLOBAL / TENANT_ALL / ORG_TREE / ORG_SELF / MEMBER_TEAM / MEMBER_SELF / CUSTOM_SQL / DENY_ALL */
//    val scopeType: String,
//    /** CUSTOM_SQL 时的表达式，如 {"where":"alias.region_code = :regionCode","params":{...}} */
//    val scopeExpression: String = "{}",
//    /** ALLOW 放行 / DENY 拒绝（优先级最高） */
//    val effect: String = "ALLOW",
//    val priority: Int = 0,
//    val builtin: Boolean = true,
//    val status: Int = 1, // smallint
//    val description: String? = null,
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
///**
// * 策略绑定：超管把系统策略挂到全局角色 / 角色 / 成员 / 权限点上
// *
// * 注意：DDL 上有表达式唯一索引 uk_dsb_subject_scope，
// *      (policy_id, subject_type, subject_id, COALESCE(app_id,0), COALESCE(tenant_id,0)) 不可重复。
// */
//data class DataScopeBinding(
//    val id: Long = 0,
//    val policyId: Long,
//    /** GLOBAL_ROLE / ROLE / MEMBER / PERMISSION */
//    val subjectType: String,
//    val subjectId: Long,
//    /** NULL = 不限应用 */
//    val appId: Long? = null,
//    /** NULL = 系统级（超管分配）；有值 = 租户级收窄 */
//    val tenantId: Long? = null,
//    /** 租户级绑定可收窄的上限，防止越权放大 */
//    val maxScopeType: String? = null,
//    val priority: Int = 0,
//    val status: Int = 1, // smallint
//    override val createdAt: OffsetDateTime,
//    override val updatedAt: OffsetDateTime? = null,
//    override val deletedAt: OffsetDateTime? = null,
//    override val createdBy: Long? = null,
//    override val updatedBy: Long? = null,
//    override val deletedBy: Long? = null,
//    override val deleted: Boolean = false,
//) : BaseEntity
