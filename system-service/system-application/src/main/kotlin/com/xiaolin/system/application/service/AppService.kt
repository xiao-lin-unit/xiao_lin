package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.AppCreateCommand
import com.xiaolin.system.domain.aggregation.AppAggregation
import com.xiaolin.system.domain.repository.AppRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AppService(
    private val appRepository: AppRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: AppCreateCommand): AppAggregation {
        require(!appRepository.existsByCode(command.code)) { "应用编码已存在: ${command.code}" }

        val app = AppAggregation.create(idGenerator.nextId())
            .code(command.code)
            .name(command.name)
            .type(command.type)
            .entryUrl(command.entryUrl)
            .icon(command.icon)
            .description(command.description)
            .status(command.status)
            .builtin(command.builtin)
            .sortNo(command.sortNo)
            .build()

        app.verify()
        appRepository.create(app)
        return app
    }

    @Transactional
    fun update(id: Long, command: AppCreateCommand): AppAggregation {
        val app = appRepository.getById(id)
        require(!app.deleted) { "应用已删除: $id" }
        require(!appRepository.existsByCode(command.code, excludeId = id)) {
            "应用编码已存在: ${command.code}"
        }

        app.code = command.code
        app.name = command.name
        app.type = command.type
        app.entryUrl = command.entryUrl
        app.icon = command.icon
        app.description = command.description
        command.status?.let { app.status = it }
        command.builtin?.let { app.builtin = it }
        command.sortNo?.let { app.sortNo = it }

        app.verify()
        appRepository.update(app)
        return app
    }

    @Transactional
    fun delete(id: Long) {
        val app = appRepository.getById(id)
        app.verifyDeletable()
        appRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): AppAggregation = appRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<AppAggregation> = appRepository.list(pageable)
}
