package com.sappyoak.konscriptor.core.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.whileSelect

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> ReceiveChannel<T>.receiveBatch(
    timeout: Long,
    sizeLimit: Int
): List<T> {
    val batch = mutableListOf<T>()
    whileSelect {
        onTimeout(timeout) { false }
        onReceiveCatching { result ->
            result.onFailure { if (it != null) throw it }
                .onClosed { return@onReceiveCatching false }
                .onSuccess { batch += it }
            batch.size < sizeLimit
        }
    }
    return batch
}