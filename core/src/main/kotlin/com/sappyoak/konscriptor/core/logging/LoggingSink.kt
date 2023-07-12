package com.sappyoak.konscriptor.core.logging

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch

import com.sappyoak.konscriptor.core.Constants.DEFAULT_LOG_BATCH_SIZE
import com.sappyoak.konscriptor.core.Constants.DEFAULT_LOG_EVENT_CHANNEL_SIZE
import com.sappyoak.konscriptor.core.Constants.DEFAULT_LOG_TIMEOUT_MS
import com.sappyoak.konscriptor.core.utils.receiveBatch

typealias EventBatchSender = suspend (List<String>) -> Unit

class LoggingSink(private val scope: CoroutineScope, private val sender: EventBatchSender) {
    private val channel: Channel<String> by lazy {
        val channel = Channel<String>(
            DEFAULT_LOG_EVENT_CHANNEL_SIZE)
        scope.launch {
            while (true) {
                val batch = channel.receiveBatch(DEFAULT_LOG_TIMEOUT_MS, DEFAULT_LOG_BATCH_SIZE)
                if (batch.isNotEmpty()) {
                    sender(batch)
                }
            }
        }
        channel
    }

    suspend fun send(event: String) {
        channel.send(event)
    }

    suspend fun sendDirect(event: String) {
        sender(listOf(event))
    }
}