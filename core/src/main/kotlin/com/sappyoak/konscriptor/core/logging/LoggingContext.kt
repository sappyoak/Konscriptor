package com.sappyoak.konscriptor.core.logging


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

import com.sappyoak.konscriptor.core.Constants.DEFAULT_LOG_EVENT_CHANNEL_SIZE

class LoggingContext(private val rootDelegate: DelegateLogger, scope: CoroutineScope) {
    private val channel: Channel<LogEvent> = Channel(capacity = DEFAULT_LOG_EVENT_CHANNEL_SIZE)
    private val loggers: MutableMap<LoggerName, Logger> = ConcurrentHashMap()
    private val sinks: CopyOnWriteArraySet<LoggingSink> = CopyOnWriteArraySet()

    init {
        scope.launch {
            channel.consumeEach { onReceive(it) }
        }
    }

    fun consumeEvent(event: LogEvent) {
        channel.trySend(event)
    }

    fun getLogger(name: LoggerName): Logger {
        val logger = loggers.computeIfAbsent(name) {
            Logger(name, this, rootDelegate.getLogger(name.value))
        }
        return logger
    }

    fun getLogger(name: String): Logger = getLogger(LoggerName(name))

    private suspend fun onReceive(event: LogEvent) {

    }
}