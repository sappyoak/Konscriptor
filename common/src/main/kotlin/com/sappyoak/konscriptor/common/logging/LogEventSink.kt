package com.sappyoak.konscriptor.common.logging

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

typealias StringSender = suspend (String) -> Unit
typealias EventBatchSender = suspend (List<LogEvent>) -> Unit

open class LogEventSink(
    val name: String,
    val sender: EventBatchSender,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val sinkChannel: Channel<LogEvent> by lazy {
        val channel = Channel<LogEvent>(150)
        launch(CoroutineName("logging-sink-$name")) {
            while (true) {
                val batch = receiveBatch(channel, 10, 100)
                if (batch.isNotEmpty()) {
                    sender(batch)
                }
            }
        }
        channel
    }

    suspend fun send(event: LogEvent) {
        sinkChannel.send(event)
    }

    suspend fun sendDirect(event: LogEvent) {
        sender(listOf(event))
    }
}