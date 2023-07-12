package com.sappyoak.konscriptor.core.scheduler

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers as KDispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor

private lateinit var SyncDispatcher: PlatformCoroutineDispatcher
private lateinit var AsyncDispatcher: PlatformCoroutineDispatcher
private lateinit var InternalWorkDispatcher: CoroutineDispatcher

fun providePlatformDispatchersSimple(scheduler: Scheduler, executor: Executor) {
    if (::SyncDispatcher.isInitialized.not()) {
        SyncDispatcher = PlatformCoroutineDispatcher(scheduler, false)
    }
    if (::AsyncDispatcher.isInitialized.not()) {
        AsyncDispatcher = PlatformCoroutineDispatcher(scheduler, true)
    }
    if (::InternalWorkDispatcher.isInitialized.not()) {
        InternalWorkDispatcher = executor.asCoroutineDispatcher()
    }
}


object Dispatchers {
    val Default: CoroutineDispatcher = KDispatchers.Default
    val IO: CoroutineDispatcher = KDispatchers.IO
    val Sync: CoroutineDispatcher get() = SyncDispatcher
    val Async: CoroutineDispatcher get() = AsyncDispatcher
    val Internal: CoroutineDispatcher get() = InternalWorkDispatcher
}