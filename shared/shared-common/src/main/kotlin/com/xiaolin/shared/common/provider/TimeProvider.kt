package com.xiaolin.shared.common.provider

import java.time.Clock
import java.time.OffsetDateTime

/**
 * 全局时间源。
 *
 * updated_at 改由应用层维护后，建议统一通过它取时间：
 *   - 生产环境默认 Clock.systemUTC()；
 *   - 单测里可替换为固定 Clock，让断言可复现。
 *
 * 用法：TimeProvider.clock = Clock.fixed(instant, ZoneOffset.UTC)
 */
object TimeProvider {
    @Volatile
    var clock: Clock = Clock.systemUTC()

    fun now(): OffsetDateTime = OffsetDateTime.now(clock)
}