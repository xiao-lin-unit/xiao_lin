package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.RoleCreateCommand
import com.xiaolin.system.domain.aggregation.RoleAggregation
import com.xiaolin.system.domain.repository.AppRepository
import com.xiaolin.system.domain.repository.RoleRepository
import com.xiaolin.system.domain.repository.TenantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** 角色继承链最大深度，防止误配成环后无限上溯 */
private const val MAX_ROLE_DEPTH = 32

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val tenantRepository: TenantRepository,
    private val appRepository: AppRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: RoleCreateCommand): RoleAggregation {
        val tenantId = requireNotNull(command.tenantId) { "角色所属租户不能为空" }
        val appId = requireNotNull(command.appId) { "角色所属应用不能为空" }

        require(!roleRepository.existsByTenantAppAndCode(tenantId, appId, command.code)) {
            "该应用下角色编码已存在: ${command.code}"
        }

        tenantRepository.getById(tenantId)   // 外键校验
        appRepository.getById(appId)         // 外键校验

        command.parentId?.let { verifySameTenantAndApp(it, tenantId, appId) }

        val role = RoleAggregation.create(idGenerator.nextId())
            .tenantId(tenantId)
            .appId(appId)
            .parentId(command.parentId)
            .code(command.code)
            .name(command.name)
            .description(command.description)
            .isTenantAdmin(command.isTenantAdmin)
            .builtin(command.builtin)
            .status(command.status)
            .sortNo(command.sortNo)
            .build()

        role.verify()
        roleRepository.create(role)
        return role
    }

    @Transactional
    fun update(id: Long, command: RoleCreateCommand): RoleAggregation {
        val role = roleRepository.getById(id)
        require(!role.deleted) { "角色已删除: $id" }

        val tenantId = command.tenantId ?: role.tenantId
        val appId = command.appId ?: role.appId
        require(tenantId == role.tenantId) { "角色不允许跨租户迁移" }
        require(appId == role.appId) { "角色不允许跨应用迁移" }

        if (command.code != role.code) {
            require(!roleRepository.existsByTenantAppAndCode(tenantId, appId, command.code, excludeId = id)) {
                "该应用下角色编码已存在: ${command.code}"
            }
            role.code = command.code
        }

        if (command.parentId != role.parentId) {
            command.parentId?.let {
                verifySameTenantAndApp(it, tenantId, appId)
                verifyNoCycle(id, it)
            }
            role.changeParent(command.parentId)
        }

        role.name = command.name
        role.description = command.description
        command.isTenantAdmin?.let { role.isTenantAdmin = it }
        command.builtin?.let { role.builtin = it }
        command.status?.let { role.status = it }
        command.sortNo?.let { role.sortNo = it }

        role.verify()
        roleRepository.update(role)
        return role
    }

    @Transactional
    fun delete(id: Long) {
        val role = roleRepository.getById(id)
        val childCount = roleRepository.countByParentId(role.tenantId, id)
        role.verifyDeletable(childCount)
        roleRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): RoleAggregation = roleRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(tenantId: Long, appId: Long?, pageable: Pageable): Page<RoleAggregation> =
        roleRepository.list(tenantId, appId, pageable)

    /**
     * 展开角色继承链：从该角色一路向上取到根，返回 [自身, 父, 祖父, ...]。
     * 权限快照计算时按此顺序合并权限，子角色覆盖父角色。
     */
    @Transactional(readOnly = true)
    fun inheritanceChain(id: Long): List<RoleAggregation> {
        val chain = mutableListOf<RoleAggregation>()
        var current = roleRepository.getById(id)
        chain += current

        var depth = 0
        while (current.parentId != null) {
            require(++depth <= MAX_ROLE_DEPTH) { "角色继承链过深（超过 $MAX_ROLE_DEPTH 层），可能存在环: $id" }
            current = roleRepository.getById(current.parentId!!)
            chain += current
        }
        return chain
    }

    private fun verifySameTenantAndApp(parentId: Long, tenantId: Long, appId: Long) {
        val parent = roleRepository.getById(parentId)
        require(parent.tenantId == tenantId) { "父角色不属于当前租户" }
        require(parent.appId == appId) { "父角色不属于当前应用" }
    }

    /** 把 newParentId 一路向上走，若撞到自己说明成环 */
    private fun verifyNoCycle(selfId: Long, newParentId: Long) {
        var cursor: Long? = newParentId
        var depth = 0
        while (cursor != null) {
            require(cursor != selfId) { "角色继承不能形成环: $selfId" }
            require(++depth <= MAX_ROLE_DEPTH) { "角色继承链过深，可能存在环" }
            cursor = roleRepository.getById(cursor).parentId
        }
    }
}
