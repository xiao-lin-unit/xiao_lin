package com.xiaolin.system.domain.aggregation

import com.xiaolin.shared.common.constants.TenantStatus
import java.time.Clock
import java.time.OffsetDateTime

/**
 * 租户聚合根
 *
 * 数据隔离的基本单位。设计描述里：以村为单位的农户群体、以农贸市场为单位的商贩群体等，
 * 每个租户挂在一个租户类型下，租户内自带组织树与应用集。
 */
class TenantAggregation(
    val id: Long,
    var tenantTypeId: Long,
    /** 预留：代理商 -> 下级代理 */
    var parentId: Long? = null,
    /** 租户树路径 /1/12/135/，由 DB 触发器维护 */
    var path: String = "/",
    var levelNo: Int = 1,
    /** 租户唯一编码 */
    var code: String,
    var name: String,
    var shortName: String? = null,
    /** 行政区划码，便于按地区运营 */
    var regionCode: String? = null,
    var regionName: String? = null,
    var address: String? = null,
    var contactName: String? = null,
    var contactPhone: String? = null,
    /** 1正常 0待审核 2冻结 3注销 */
    var status: Int = TenantStatus.PENDING,
    var expireAt: OffsetDateTime? = null,
    /** 租户个性化配置 JSON（菜单、参数、开关） */
    var config: String = "{}",
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null,
    var deletedAt: OffsetDateTime? = null,
    var createdBy: Long? = null,
    var updatedBy: Long? = null,
    var deletedBy: Long? = null,
    var deleted: Boolean = false,
) {

    class TenantAggregationBuilder {
        var id: Long = 0L
        var tenantTypeId: Long = 0L
        var parentId: Long? = null
        var path: String = "/"
        var levelNo: Int = 1
        lateinit var code: String
        lateinit var name: String
        var shortName: String? = null
        var regionCode: String? = null
        var regionName: String? = null
        var address: String? = null
        var contactName: String? = null
        var contactPhone: String? = null
        var status: Int = TenantStatus.PENDING
        var expireAt: OffsetDateTime? = null
        var config: String = "{}"
        var createdAt: OffsetDateTime? = null
        var updatedAt: OffsetDateTime? = null
        var deletedAt: OffsetDateTime? = null
        var createdBy: Long? = null
        var updatedBy: Long? = null
        var deletedBy: Long? = null
        var deleted: Boolean = false

        internal fun id(id: Long) = apply { this.id = id }
        fun tenantTypeId(tenantTypeId: Long) = apply { this.tenantTypeId = tenantTypeId }
        fun parentId(parentId: Long?) = apply { this.parentId = parentId }
        fun path(path: String?) = apply { this.path = path ?: "/" }
        fun levelNo(levelNo: Int?) = apply { this.levelNo = levelNo ?: 1 }
        fun code(code: String) = apply { this.code = code }
        fun name(name: String) = apply { this.name = name }
        fun shortName(shortName: String?) = apply { this.shortName = shortName }
        fun regionCode(regionCode: String?) = apply { this.regionCode = regionCode }
        fun regionName(regionName: String?) = apply { this.regionName = regionName }
        fun address(address: String?) = apply { this.address = address }
        fun contactName(contactName: String?) = apply { this.contactName = contactName }
        fun contactPhone(contactPhone: String?) = apply { this.contactPhone = contactPhone }
        fun status(status: Int?) = apply { this.status = status ?: TenantStatus.PENDING }
        fun expireAt(expireAt: OffsetDateTime?) = apply { this.expireAt = expireAt }
        fun config(config: String?) = apply { this.config = config ?: "{}" }
        fun createdAt(createdAt: OffsetDateTime?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: OffsetDateTime?) = apply { this.updatedAt = updatedAt }
        fun deletedAt(deletedAt: OffsetDateTime?) = apply { this.deletedAt = deletedAt }
        fun createdBy(createdBy: Long?) = apply { this.createdBy = createdBy }
        fun updatedBy(updatedBy: Long?) = apply { this.updatedBy = updatedBy }
        fun deletedBy(deletedBy: Long?) = apply { this.deletedBy = deletedBy }
        fun deleted(deleted: Boolean?) = apply { this.deleted = deleted ?: false }

        fun build(): TenantAggregation = TenantAggregation(
            id = id,
            tenantTypeId = tenantTypeId,
            parentId = parentId,
            path = path,
            levelNo = levelNo,
            code = code,
            name = name,
            shortName = shortName,
            regionCode = regionCode,
            regionName = regionName,
            address = address,
            contactName = contactName,
            contactPhone = contactPhone,
            status = status,
            expireAt = expireAt,
            config = config,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            createdBy = createdBy,
            updatedBy = updatedBy,
            deletedBy = deletedBy,
            deleted = deleted,
        )
    }

    companion object {
        fun create(id: Long): TenantAggregationBuilder {
            val now = OffsetDateTime.now(Clock.systemUTC())
            return builder(id)
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
        }

        fun builder(id: Long): TenantAggregationBuilder = TenantAggregationBuilder().id(id)
    }

    /** 审核通过 */
    fun approve() = apply { status = TenantStatus.NORMAL }

    /** 冻结：数据仍在，但禁止一切写入 */
    fun freeze() = apply { status = TenantStatus.FROZEN }

    /** 解冻 */
    fun unfreeze() = apply { status = TenantStatus.NORMAL }

    /** 注销 */
    fun cancel() = apply { status = TenantStatus.CANCELLED }

    fun isDeleted(): Boolean = deleted

    fun isNormal(): Boolean = status == TenantStatus.NORMAL

    fun isFrozen(): Boolean = status == TenantStatus.FROZEN

    fun isExpired(now: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC())): Boolean =
        expireAt?.isBefore(now) ?: false

    fun del(operatorId: Long?) = apply {
        val now = OffsetDateTime.now(Clock.systemUTC())
        this.deleted = true
        this.deletedAt = now
        this.deletedBy = operatorId
        this.updatedAt = now
        this.updatedBy = operatorId
    }

    /**
     * 子节点 path 由父节点推导，DB 触发器 tg_rebuild_tenant_path 会兜底重算。
     * 这里给出的值用于 INSERT 时尽量接近最终结果，避免读到 '/'
     */
    fun derivePath(parentPath: String?): String =
        if (parentPath.isNullOrBlank()) "/$id/" else "$parentPath$id/"

    fun verify() {
        require(tenantTypeId > 0) { "租户类型不能为空" }
        require(code.isNotBlank()) { "租户编码不能为空" }
        require(name.isNotBlank()) { "租户名称不能为空" }
        require(status in TenantStatus.PENDING..TenantStatus.CANCELLED) {
            "租户状态非法：$status（允许 0待审核 1正常 2冻结 3注销）"
        }
        require(parentId != id) { "租户不能以自己作为上级" }
    }
}
