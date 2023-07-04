package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.whileSelect
import kotlin.coroutines.CoroutineContext

import com.sappyoak.konscriptor.common.DEFAULT_LOG_BATCH_SIZE
import com.sappyoak.konscriptor.common.DEFAULT_LOG_EVENT_CHANNEL_SIZE
import com.sappyoak.konscriptor.common.DEFAULT_LOG_TIMEOUT_MS

typealias StringSender = suspend (String) -> Unit
typealias EventBatchSender = suspend (List<String>) -> Unit

open class LogEventSink(
    val name: String,
    val sender: EventBatchSender,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val sinkChannel: Channel<String> by lazy {
        val channel = Channel<String>(DEFAULT_LOG_EVENT_CHANNEL_SIZE)
        launch(CoroutineName("logging-sink-$name")) {
            while (true) {
                val batch = receiveBatch(channel, DEFAULT_LOG_TIMEOUT_MS, DEFAULT_LOG_BATCH_SIZE)
                if (batch.isNotEmpty()) {
                    sender(batch)
                }
            }
        }
        channel
    }

    suspend fun send(event: String) {
        sinkChannel.send(event)
    }

    suspend fun sendDirect(event: String) {
        sender(listOf(event))
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