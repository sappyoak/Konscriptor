package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.*
import java.util.concurrent.ConcurrentHashMap

class LoggingContext(private val delegateProvider: DelegateProvider, scope: CoroutineScope) {
    private val channel: Channel<String> = Channel(capacity = 250)
    private val messages: MutableList<String> = mutableListOf()
    private val loggers: MutableMap<String, Logger> = ConcurrentHashMap()

    private val rootDelegate: DelegateLogger by lazy { delegateProvider.get() }

    init {
        scope.launch {
            channel.consumeEach { onReceive(it) }
        }
    }

    fun consumeEvent(level: LogLevel, message: String, throwable: Throwable? = null) {
        channel.trySend(message)
    }

    fun getLogger(name: String): Logger {
        if (loggers.containsKey(name)) return loggers[name]!!
        val logger = Logger(name, this, rootDelegate.getLogger(name))
        loggers[name] = logger
        return logger
    }

    // @TODO actually do some handling of the logs
    private fun onReceive(action: String) {
        messages.add(action)
    }
}

suspend fun <T> receiveBatch(
    channel: ReceiveChannel<T>,
    maxTimeMillis: Long,
    maxSize: Int
): List<T> {
    val batch = mutableListOf<T>()
    whileSelect {
        onTimeout(maxTimeMillis) { false }
        channel.onReceiveCatching { result ->
            result.onFailure { if (it != null) throw it }
                .onClosed { return@onReceiveCatching false }
                .onSuccess { batch += it }
            batch.size < maxSize
        }
    }
    return batch
}