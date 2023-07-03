package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
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