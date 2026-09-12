package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.GlobalRoleCreateCommand
import com.xiaolin.system.domain.aggregation.GlobalRoleAggregation
import com.xiaolin.system.domain.repository.GlobalRoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GlobalRoleService(
    private val globalRoleRepository: GlobalRoleRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: GlobalRoleCreateCommand): GlobalRoleAggregation {
        require(!globalRoleRepository.existsByCode(command.code)) {
            "全局角色编码已存在: ${command.code}"
        }
        // 超管角色全局唯一 —— 多个 isSuper 会让"绕过校验"的判定出现歧义
        if (command.isSuper == true) {
            val existing = globalRoleRepository.findSuper()
            require(existing == null) { "超级管理员角色已存在: ${existing?.code}" }
        }

        val globalRole = GlobalRoleAggregation.create(idGenerator.nextId())
            .code(command.code)
            .name(command.name)
            .description(command.description)
            .isSuper(command.isSuper)
            .builtin(command.builtin)
            .status(command.status)
            .build()

        globalRole.verify()
        globalRoleRepository.create(globalRole)
        return globalRole
    }

    @Transactional
    fun update(id: Long, command: GlobalRoleCreateCommand): GlobalRoleAggregation {
        val globalRole = globalRoleRepository.getById(id)
        require(!globalRole.deleted) { "全局角色已删除: $id" }
        require(!globalRoleRepository.existsByCode(command.code, excludeId = id)) {
            "全局角色编码已存在: ${command.code}"
        }
        if (command.isSuper == true && !globalRole.isSuper) {
            val existing = globalRoleRepository.findSuper()
            require(existing == null) { "超级管理员角色已存在: ${existing?.code}" }
        }

        globalRole.code = command.code
        globalRole.name = command.name
        globalRole.description = command.description
        command.isSuper?.let { globalRole.isSuper = it }
        command.builtin?.let { globalRole.builtin = it }
        command.status?.let { globalRole.status = it }

        globalRole.verify()
        globalRoleRepository.update(globalRole)
        return globalRole
    }

    @Transactional
    fun delete(id: Long) {
        val globalRole = globalRoleRepository.getById(id)
        globalRole.verifyDeletable()
        globalRoleRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): GlobalRoleAggregation = globalRoleRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<GlobalRoleAggregation> = globalRoleRepository.list(pageable)
}
