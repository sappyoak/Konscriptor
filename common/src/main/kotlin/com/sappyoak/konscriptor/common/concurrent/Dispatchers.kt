package com.sappyoak.konscriptor.common.concurrent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers as KDispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor

private lateinit var SyncDispatcher: PlatformDispatcher
private lateinit var AsyncDispatcher: PlatformDispatcher
private lateinit var InternalDispatcher: CoroutineDispatcher


fun initializeDispatchers(scheduler: Scheduler, internalPool: Executor) {
    if (::SyncDispatcher.isInitialized.not()) {
        SyncDispatcher = PlatformDispatcher(scheduler, false)
    }
    if (::AsyncDispatcher.isInitialized.not()) {
        AsyncDispatcher = PlatformDispatcher(scheduler, true)
    }
    if (::InternalDispatcher.isInitialized.not()) {
        InternalDispatcher = internalPool.asCoroutineDispatcher()
    }
}

object Dispatchers {
    val Default: CoroutineDispatcher = KDispatchers.Default
    val IO: CoroutineDispatcher = KDispatchers.IO
    val Sync: CoroutineDispatcher get() = SyncDispatcher
    val Async: CoroutineDispatcher get() = AsyncDispatcher
    val Internal: CoroutineDispatcher get() = InternalDispatcher
}