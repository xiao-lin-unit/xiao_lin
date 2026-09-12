package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.TenantCreateCommand
import com.xiaolin.system.domain.aggregation.TenantAggregation
import com.xiaolin.system.domain.repository.TenantRepository
import com.xiaolin.system.domain.repository.TenantTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TenantService(
    private val tenantRepository: TenantRepository,
    private val tenantTypeRepository: TenantTypeRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: TenantCreateCommand): TenantAggregation {
        require(!tenantRepository.existsByCode(command.code)) { "租户编码已存在: ${command.code}" }

        val tenantTypeId = requireNotNull(command.tenantTypeId) { "租户类型不能为空" }
        // 外键 sys.tenant -> sys.tenant_type，提前在应用层挡住，避免直接撞 DB 约束
        val tenantType = tenantTypeRepository.getById(tenantTypeId)

        val parentPath = command.parentId?.let { tenantRepository.getById(it).path }
        val id = idGenerator.nextId()

        val tenant = TenantAggregation.create(id)
            .tenantTypeId(tenantTypeId)
            .parentId(command.parentId)
            .code(command.code)
            .name(command.name)
            .shortName(command.shortName)
            .regionCode(command.regionCode)
            .regionName(command.regionName)
            .address(command.address)
            .contactName(command.contactName)
            .contactPhone(command.contactPhone)
            .status(command.status)
            .expireAt(command.expireAt)
            .config(command.config)
            .build()

        // path / levelNo 由触发器 tg_rebuild_tenant_path 兜底重算，这里给出初值避免读到 '/'
        tenant.path = tenant.derivePath(parentPath)
        tenant.levelNo = if (command.parentId == null) 1 else parentPath!!.trim('/').split('/').size + 1

        tenant.verify()

        // TODO 按 tenantType.orgLevels 模板初始化默认组织树，交给 OrgService 承接
        tenantRepository.create(tenant)
        return tenant
    }

    @Transactional
    fun update(id: Long, command: TenantCreateCommand): TenantAggregation {
        val tenant = tenantRepository.getById(id)
        require(!tenant.deleted) { "租户已删除: $id" }
        require(!tenantRepository.existsByCode(command.code, excludeId = id)) {
            "租户编码已存在: ${command.code}"
        }

        command.tenantTypeId?.let {
            tenantTypeRepository.getById(it)   // 校验存在性
            tenant.tenantTypeId = it
        }
        command.parentId?.let {
            require(it != id) { "租户不能以自己作为上级" }
            tenant.parentId = it
        }
        tenant.code = command.code
        tenant.name = command.name
        tenant.shortName = command.shortName
        tenant.regionCode = command.regionCode
        tenant.regionName = command.regionName
        tenant.address = command.address
        tenant.contactName = command.contactName
        tenant.contactPhone = command.contactPhone
        command.status?.let { tenant.status = it }
        tenant.expireAt = command.expireAt
        tenant.config = command.config ?: "{}"

        tenant.verify()
        tenantRepository.update(tenant)
        return tenant
    }

    @Transactional
    fun delete(id: Long) {
        val tenant = tenantRepository.getById(id)
        require(!tenant.deleted) { "租户已删除: $id" }

        val children = tenantRepository.listByParentId(id)
        require(children.isEmpty()) { "租户下仍有 ${children.size} 个下级租户，不可删除" }

        tenantRepository.deleteById(id)
    }

    /** 审核通过 -> 正常 */
    @Transactional
    fun approve(id: Long): TenantAggregation {
        val tenant = tenantRepository.getById(id)
        tenant.approve()
        tenantRepository.update(tenant)
        return tenant
    }

    @Transactional
    fun freeze(id: Long): TenantAggregation {
        val tenant = tenantRepository.getById(id)
        tenant.freeze()
        tenantRepository.update(tenant)
        return tenant
    }

    @Transactional
    fun unfreeze(id: Long): TenantAggregation {
        val tenant = tenantRepository.getById(id)
        tenant.unfreeze()
        tenantRepository.update(tenant)
        return tenant
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): TenantAggregation = tenantRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(tenantTypeId: Long?, pageable: Pageable): Page<TenantAggregation> =
        tenantRepository.list(tenantTypeId, pageable)

    /** 租户子树，支撑"代理商看下级"的数据范围 */
    @Transactional(readOnly = true)
    fun subtree(id: Long): List<TenantAggregation> {
        val root = tenantRepository.getById(id)
        return tenantRepository.listByPathPrefix(root.path)
    }
}
