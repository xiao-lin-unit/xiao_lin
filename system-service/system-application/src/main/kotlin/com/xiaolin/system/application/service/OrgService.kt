package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.OrgCreateCommand
import com.xiaolin.system.domain.aggregation.OrgAggregation
import com.xiaolin.system.domain.repository.OrgRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrgService(
    private val orgRepository: OrgRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: OrgCreateCommand): OrgAggregation {
        val tenantId = requireNotNull(command.tenantId) { "组织所属租户不能为空" }
        command.code?.let {
            require(!orgRepository.existsByTenantAndCode(tenantId, it)) {
                "该租户下组织编码已存在: $it"
            }
        }

        // 上级组织必须存在且同租户，防止跨租户挂树
        val parent = command.parentId?.let { orgRepository.getById(it) }
        parent?.let {
            require(it.tenantId == tenantId) { "上级组织不属于当前租户" }
        }

        val org = OrgAggregation.create(idGenerator.nextId())
            .tenantId(tenantId)
            .parentId(command.parentId)
            .code(command.code)
            .name(command.name)
            .leaderMemberId(command.leaderMemberId)
            .sortNo(command.sortNo)
            .status(command.status)
            .build()

        // 触发器 trg_org_path 是 BEFORE INSERT，会用 NEW.id 重算 path / level_no 并覆盖此处初值。
        // DDL 中 path 为 NOT NULL 且无默认值，INSERT 必须给值，否则直接报错
        org.path = parent?.let { "${it.path}${org.id}/" } ?: "/${org.id}/"
        org.levelNo = parent?.let { it.levelNo + 1 } ?: 1

        org.verify()
        orgRepository.create(org)
        return org
    }

    @Transactional
    fun update(id: Long, command: OrgCreateCommand): OrgAggregation {
        val org = orgRepository.getById(id)
        require(!org.deleted) { "组织已删除: $id" }

        val tenantId = command.tenantId ?: org.tenantId
        require(tenantId == org.tenantId) { "组织不允许跨租户迁移" }

        command.code?.let {
            require(!orgRepository.existsByTenantAndCode(tenantId, it, excludeId = id)) {
                "该租户下组织编码已存在: $it"
            }
            org.code = it
        }

        // 移动节点：path / levelNo 交给触发器级联修正，应用层只改 parentId
        if (command.parentId != org.parentId) {
            command.parentId?.let {
                val parent = orgRepository.getById(it)
                require(parent.tenantId == org.tenantId) { "上级组织不属于当前租户" }
                require(it != id) { "组织不能挂到自己下面" }
            }
            org.moveTo(command.parentId)
        }

        org.name = command.name
        org.leaderMemberId = command.leaderMemberId
        command.sortNo?.let { org.sortNo = it }
        command.status?.let { org.status = it }

        org.verify()
        orgRepository.update(org)
        return org
    }

    @Transactional
    fun delete(id: Long) {
        val org = orgRepository.getById(id)
        val childCount = orgRepository.countByParentId(org.tenantId, id)
        org.verifyDeletable(childCount)
        orgRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): OrgAggregation = orgRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(tenantId: Long, pageable: Pageable): Page<OrgAggregation> =
        orgRepository.list(tenantId, pageable)

    /** 指定组织的整棵子树，支撑 ORG_TREE 数据范围 */
    @Transactional(readOnly = true)
    fun subtree(id: Long): List<OrgAggregation> {
        val root = orgRepository.getById(id)
        return orgRepository.listByPathPrefix(root.tenantId, root.path)
    }

    /** 直接子节点，前端树懒加载用 */
    @Transactional(readOnly = true)
    fun children(tenantId: Long, parentId: Long?): List<OrgAggregation> =
        orgRepository.listByParentId(tenantId, parentId)
}
