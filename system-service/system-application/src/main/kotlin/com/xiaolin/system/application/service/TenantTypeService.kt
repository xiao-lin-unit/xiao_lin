package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.TenantTypeCreateCommand
import com.xiaolin.system.domain.aggregation.TenantTypeAggregation
import com.xiaolin.system.domain.repository.TenantRepository
import com.xiaolin.system.domain.repository.TenantTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TenantTypeService(
    private val tenantTypeRepository: TenantTypeRepository,
    private val tenantRepository: TenantRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: TenantTypeCreateCommand): TenantTypeAggregation {
        require(!tenantTypeRepository.existsByCode(command.code)) {
            "租户类型编码已存在: ${command.code}"
        }

        val tenantType = TenantTypeAggregation.create(idGenerator.nextId())
            .code(command.code)
            .name(command.name)
            .description(command.description)
            .orgLevels(command.orgLevels)
            .defaultApps(command.defaultApps)
            .status(command.status)
            .builtin(command.builtin)
            .sortNo(command.sortNo)
            .build()

        tenantType.verify()
        tenantTypeRepository.create(tenantType)
        return tenantType
    }

    @Transactional
    fun update(id: Long, command: TenantTypeCreateCommand): TenantTypeAggregation {
        val tenantType = tenantTypeRepository.getById(id)
        require(!tenantType.deleted) { "租户类型已删除: $id" }
        require(!tenantTypeRepository.existsByCode(command.code, excludeId = id)) {
            "租户类型编码已存在: ${command.code}"
        }

        tenantType.code = command.code
        tenantType.name = command.name
        tenantType.description = command.description
        tenantType.orgLevels = command.orgLevels ?: "[]"
        tenantType.defaultApps = command.defaultApps ?: "[]"
        command.status?.let { tenantType.status = it }
        command.builtin?.let { tenantType.builtin = it }
        command.sortNo?.let { tenantType.sortNo = it }

        tenantType.verify()
        tenantTypeRepository.update(tenantType)
        return tenantType
    }

    @Transactional
    fun delete(id: Long) {
        val tenantType = tenantTypeRepository.getById(id)
        tenantType.verifyDeletable()

        // 仍被引用的类型不能直接删，否则既有租户会失去归属
        val used = tenantRepository.countByTenantTypeId(id)
        require(used == 0L) { "该租户类型下仍有 $used 个租户，不可删除" }

        tenantTypeRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): TenantTypeAggregation = tenantTypeRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<TenantTypeAggregation> = tenantTypeRepository.list(pageable)
}
