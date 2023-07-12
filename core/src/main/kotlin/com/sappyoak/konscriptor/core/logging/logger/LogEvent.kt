package com.sappyoak.konscriptor.core.logging.logger

import com.sappyoak.konscriptor.core.logging.LogLevel
import com.sappyoak.konscriptor.core.utils.milliseconds
import com.sappyoak.konscriptor.core.utils.nanoseconds
import com.sappyoak.konscriptor.core.utils.randomId

typealias LogEventMeta = Map<String, Any?>
typealias MutableLogEventMeta = MutableMap<String, Any?>
typealias EventBuilder = LogEventBuilder.() -> Unit
typealias MessageProducer = () -> String

data class LogEvent(
    val id: String = randomId(),
    val timestamp: Double = System.currentTimeMillis().milliseconds.nanoseconds,
    val logger: LoggerName,
    val level: LogLevel = LogLevel.Info,
    val message: String,
    val throwable: Throwable? = null,
    val meta: LogEventMeta = mapOf()
)

inline fun buildLogEvent(block: EventBuilder): LogEvent = LogEventBuilder().apply(block).build()

class LogEventBuilder {
    var id: String = randomId()
    var timestamp: Double = System.currentTimeMillis().milliseconds.nanoseconds
    var logger: LoggerName? = null
    var level: LogLevel = LogLevel.Info
    var message: String? = null
    var throwable: Throwable? = null

    val meta: MutableLogEventMeta = mutableMapOf()

    // @TODO: Proper guards on passed [MessageProducer]s throwing errors
    inline fun message(block: MessageProducer) {
        message = block()
    }

    fun logger(name: String) {
        logger = LoggerName(name)
    }

    fun meta(map: LogEventMeta) {
        meta.putAll(map)
    }

    fun meta(vararg props: Pair<String, Any?>) {
        meta.putAll(props)
    }

    fun meta(pair: Pair<String, Any?>) {
        meta[pair.first] = pair.second
    }

    fun build(): LogEvent = LogEvent(
        id = id,
        timestamp = timestamp,
        logger = logger ?: UnknownLoggerName,
        level = level,
        message = requireNotNull(message) { "Every log event must have an associated message" },
        throwable = throwable,
        meta = meta
    )

    companion object {
        private val UnknownLoggerName: LoggerName = LoggerName("UnknownLogger")
    }
}