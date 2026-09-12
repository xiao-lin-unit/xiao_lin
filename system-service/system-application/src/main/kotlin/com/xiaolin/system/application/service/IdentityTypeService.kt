package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.IdentityTypeCreateCommand
import com.xiaolin.system.domain.aggregation.IdentityTypeAggregation
import com.xiaolin.system.domain.repository.IdentityTypeRepository
import com.xiaolin.system.domain.repository.TenantTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IdentityTypeService(
    private val identityTypeRepository: IdentityTypeRepository,
    private val tenantTypeRepository: TenantTypeRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: IdentityTypeCreateCommand): IdentityTypeAggregation {
        require(!identityTypeRepository.existsByCode(command.code)) {
            "身份类型编码已存在: ${command.code}"
        }
        // 外键 sys.identity_type -> sys.tenant_type
        command.tenantTypeId?.let { tenantTypeRepository.getById(it) }

        val identityType = IdentityTypeAggregation.create(idGenerator.nextId())
            .code(command.code)
            .name(command.name)
            .tenantTypeId(command.tenantTypeId)
            .description(command.description)
            .builtin(command.builtin)
            .sortNo(command.sortNo)
            .build()

        identityType.verify()
        identityTypeRepository.create(identityType)
        return identityType
    }

    @Transactional
    fun update(id: Long, command: IdentityTypeCreateCommand): IdentityTypeAggregation {
        val identityType = identityTypeRepository.getById(id)
        require(!identityType.deleted) { "身份类型已删除: $id" }
        require(!identityTypeRepository.existsByCode(command.code, excludeId = id)) {
            "身份类型编码已存在: ${command.code}"
        }
        command.tenantTypeId?.let { tenantTypeRepository.getById(it) }

        identityType.code = command.code
        identityType.name = command.name
        identityType.tenantTypeId = command.tenantTypeId
        identityType.description = command.description
        command.builtin?.let { identityType.builtin = it }
        command.sortNo?.let { identityType.sortNo = it }

        identityType.verify()
        identityTypeRepository.update(identityType)
        return identityType
    }

    @Transactional
    fun delete(id: Long) {
        val identityType = identityTypeRepository.getById(id)
        identityType.verifyDeletable()
        identityTypeRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): IdentityTypeAggregation = identityTypeRepository.getById(id)

    /**
     * tenantTypeId 为空时只返回通用身份；有值时返回「通用 + 该类型专属」
     */
    @Transactional(readOnly = true)
    fun list(tenantTypeId: Long?, pageable: Pageable): Page<IdentityTypeAggregation> =
        identityTypeRepository.list(tenantTypeId, pageable)
}
