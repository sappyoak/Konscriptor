package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.CoroutineName
import kotlinx.serialization.Serializable
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.random.nextUInt

typealias LogEventMeta = Map<String, String>

@Serializable
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

internal fun randomId(): String = Random.nextUInt().toString(16)
internal fun threadContext(): String = Thread.currentThread().name
suspend fun LogEvent.contextName(): String {
    return listOfNotNull(threadContext(), coroutineContext[CoroutineName]?.name).joinToString("+")
}
