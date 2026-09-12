package com.xiaolin.system.infra.converter

import com.xiaolin.system.domain.aggregation.AppAggregation
import com.xiaolin.system.infra.persistence.App

object AppConverter {

    @JvmStatic
    fun aggregation2DO(app: AppAggregation): App = App(
        id = app.id,
        code = app.code,
        name = app.name,
        type = app.type,
        entryUrl = app.entryUrl,
        icon = app.icon,
        description = app.description,
        status = app.status,
        builtin = app.builtin,
        sortNo = app.sortNo,
        createdAt = app.createdAt,
        updatedAt = app.updatedAt,
        deletedAt = app.deletedAt,
        createdBy = app.createdBy,
        updatedBy = app.updatedBy,
        deletedBy = app.deletedBy,
        deleted = app.deleted,
    )

    @JvmStatic
    fun do2Aggregation(app: App): AppAggregation =
        AppAggregation.builder(app.id)
            .code(app.code)
            .name(app.name)
            .type(app.type)
            .entryUrl(app.entryUrl)
            .icon(app.icon)
            .description(app.description)
            .status(app.status)
            .builtin(app.builtin)
            .sortNo(app.sortNo)
            .createdAt(app.createdAt)
            .updatedAt(app.updatedAt)
            .deletedAt(app.deletedAt)
            .createdBy(app.createdBy)
            .updatedBy(app.updatedBy)
            .deletedBy(app.deletedBy)
            .deleted(app.deleted)
            .build()
}
