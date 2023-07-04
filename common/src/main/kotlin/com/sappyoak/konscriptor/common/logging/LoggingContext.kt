package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlin.reflect.KClass
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

import com.sappyoak.konscriptor.common.DEFAULT_LOG_EVENT_CHANNEL_SIZE

class LoggingContext(private val rootDelegate: DelegateLogger, scope: CoroutineScope) {
    private val channel: Channel<LogEvent> = Channel(capacity = DEFAULT_LOG_EVENT_CHANNEL_SIZE)
    private val loggers: MutableMap<String, Logger> = ConcurrentHashMap()
    private val sinks: MutableSet<LogEventSink> = CopyOnWriteArraySet()

    init {
        scope.launch {
            channel.consumeEach { onReceive(it) }
        }
    }

    fun consumeEvent(event: LogEvent) {
        channel.trySend(event)
    }

    fun getLogger(name: String): Logger {
        if (loggers.containsKey(name)) return loggers[name]!!
        val logger = Logger(name, this, rootDelegate.getLogger(name))
        loggers[name] = logger
        return logger
    }

    fun getLogger(klass: KClass<*>): Logger {
        return getLogger(getClassNameOf(klass) ?: "UnknownLogger")
    }

    inline fun <reified T> getLogger(): Logger = getLogger(T::class)

    private suspend fun onReceive(action: LogEvent) {
        val serialized = serializeLogEvent(action)
        for (sink in sinks) {
            sink.send(serialized)
        }
    }
}

internal fun getClassNameOf(owner: KClass<*>): String? {
    return if (owner.isCompanion) owner.java.enclosingClass.name
    else owner.java.name
}
