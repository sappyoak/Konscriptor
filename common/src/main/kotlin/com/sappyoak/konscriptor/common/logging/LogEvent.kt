package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.random.nextUInt

import com.sappyoak.konscriptor.common.serialization.serializeMap

typealias LogEventMeta = Map<String, Any?>

data class LogEvent(
    val id: String = randomId(),
    val timestamp: Long = System.currentTimeMillis(),
    val logger: String? = null,
    val level: LogLevel,
    val message: String,
    val threadContext: String? = threadContext(),
    val meta: LogEventMeta = mapOf(),
    val stackTrace: String? = null,
) {
    fun copyWith(
        updatedLevel: LogLevel? = null,
        updatedStackTrace: String? = null,
        updatedMeta: LogEventMeta = mapOf()
    ): LogEvent = LogEvent(
        id = id,
        timestamp = timestamp,
        logger = logger,
        level = updatedLevel ?: level,
        message = message,
        threadContext = threadContext ?: threadContext(),
        meta = meta + updatedMeta,
        stackTrace = updatedStackTrace ?: stackTrace
    )
}

fun buildLogEvent(level: LogLevel, message: String, throwable: Throwable? = null, name: String? = null, meta: LogEventMeta = emptyMap()): LogEvent {
    return LogEvent(
        level = level,
        logger = name,
        message = message,
        meta = meta,
        stackTrace = parseStackFromThrowable(throwable)
    )
}

inline fun buildLogEvent(block: LogEventBuilder.() -> Unit): LogEvent {
    return LogEventBuilder().apply(block).build()
}

class LogEventBuilder {
    var id: String = randomId()
    var timestamp: Long = System.currentTimeMillis()
    var logger: String? = null
    var level: LogLevel = LogLevel.Info
    var message: String? = null
    var threadContext: String? = threadContext()
    var stackTrace: String? = null

    val meta: MutableMap<String, Any?> = mutableMapOf()

    inline fun message(block: () -> String) {
        message = block()
    }

    fun meta(map: Map<String, Any?>) {
        meta.putAll(map)
    }

    fun stackTrace(throwable: Throwable?) {
        stackTrace = parseStackFromThrowable(throwable)
    }

    inline fun meta(block: LogEventMetaBuilder.() -> Unit) {
        val values = LogEventMetaBuilder().apply(block).build()
        meta.putAll(values)
    }

    fun build(): LogEvent = LogEvent(
        id = id,
        timestamp = timestamp,
        logger = logger,
        level = level,
        message = requireNotNull(message) { "Every log event must have an associated message" },
        meta = meta,
        threadContext = threadContext,
        stackTrace = stackTrace
    )
}

class LogEventMetaBuilder {
    private val meta = mutableMapOf<String, Any?>()

    fun String.to(value: Any?) {
        meta[this] = value
    }

    fun build() = meta.toMap()
}

fun serializeLogEvent(event: LogEvent): String {
    val eventMap: MutableMap<String, Any?> = mutableMapOf(
        "@timestamp" to event.timestamp,
        "log.logger" to event.logger,
        "log.level" to event.level.name,
        "message" to event.message,
        "error.stack_trace" to event.stackTrace
    )
    if (event.threadContext != null) {
        eventMap["thread.name"] = event.threadContext
    }
    if (event.meta.isNotEmpty()) {
        eventMap += "meta" to event.meta
    }

    return serializeMap(eventMap.filterValues { it != null })
}

internal fun parseStackFromThrowable(throwable: Throwable? = null): String {
    if (throwable == null) return ""
    return (throwable.message ?: "") + "\n${throwable.stackTraceToString()}"
}

internal fun randomId(): String = Random.nextUInt().toString(16)
internal fun threadContext(): String = Thread.currentThread().name
suspend fun LogEvent.contextName(): String {
    return listOfNotNull(threadContext(), coroutineContext[CoroutineName]?.name).joinToString("+")
}
