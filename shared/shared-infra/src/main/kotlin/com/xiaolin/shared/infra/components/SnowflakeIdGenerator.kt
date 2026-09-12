package com.xiaolin.shared.infra.components

import com.xiaolin.shared.infra.properties.IdGeneratorProperties
import org.springframework.stereotype.Component

@Component
class SnowflakeIdGenerator(
    private val property: IdGeneratorProperties
) : IdGenerator {

    companion object {

        // 各部分占用位数
        private const val DATACENTER_ID_BITS = 5
        private const val WORKER_ID_BITS = 5
        private const val SEQUENCE_BITS = 12

        // 各部分最大值
        private const val MAX_DATACENTER_ID = (1L shl DATACENTER_ID_BITS) - 1  // 31
        private const val MAX_WORKER_ID = (1L shl WORKER_ID_BITS) - 1          // 31
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1            // 4095

        // 各部分左移位数
        private const val WORKER_ID_SHIFT = SEQUENCE_BITS                              // 12
        private const val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS         // 17
        private const val TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS  // 22
    }

    // 上一次生成 ID 的时间戳
    private var lastTimestamp = -1L

    // 当前毫秒内的序列号
    private var sequence = 0L

    init {
        require(property.getDatacenterId() in 0..MAX_DATACENTER_ID) {
            "数据中心ID必须在 0~$MAX_DATACENTER_ID 之间，当前值：${property.getDatacenterId()}"
        }
        require(property.getWorkerId() in 0..MAX_WORKER_ID) {
            "机器ID必须在 0~$MAX_WORKER_ID 之间，当前值：${property.getWorkerId()}"
        }
    }

    /**
     * 生成下一个唯一 ID（线程安全）
     */
    @Synchronized
    override fun nextId(): Long {
        var currentTimestamp = System.currentTimeMillis()

        // 时钟回拨检测
        if (currentTimestamp < lastTimestamp) {
            throw RuntimeException(
                "时钟回拨，拒绝生成ID。当前时间戳：$currentTimestamp，上次时间戳：$lastTimestamp"
            )
        }

        if (currentTimestamp == lastTimestamp) {
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) and MAX_SEQUENCE
            if (sequence == 0L) {
                // 当前毫秒序列号用尽，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp)
            }
        } else {
            // 新的毫秒，序列号重置
            sequence = 0L
        }

        lastTimestamp = currentTimestamp

        // 位运算拼接最终 ID
        return ((currentTimestamp - property.getEpoch()) shl TIMESTAMP_SHIFT) or
                (property.getDatacenterId() shl DATACENTER_ID_SHIFT) or
                (property.getWorkerId() shl WORKER_ID_SHIFT) or
                sequence
    }

    /**
     * 自旋等待到下一毫秒
     */
    private fun waitNextMillis(lastTs: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTs) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}