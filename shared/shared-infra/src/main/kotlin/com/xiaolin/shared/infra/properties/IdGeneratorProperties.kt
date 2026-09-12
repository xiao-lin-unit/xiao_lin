package com.xiaolin.shared.infra.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "id.generator")
data class IdGeneratorProperties(
    private val workerId: Long = 0,
    private val datacenterId: Long = 0,
    private val epoch: Long? = null
) {
    fun getWorkerId(): Long = workerId
    fun getDatacenterId(): Long = datacenterId
    fun getEpoch(): Long = epoch ?: DEFAULT_EPOCH
    companion object {
        // 默认起始时间戳：2024-01-01 00:00:00（毫秒）
        const val DEFAULT_EPOCH = 1704067200000L
    }
}
