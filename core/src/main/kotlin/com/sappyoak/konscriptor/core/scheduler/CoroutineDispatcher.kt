package com.sappyoak.konscriptor.core.scheduler

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

import com.sappyoak.konscriptor.core.utils.milliseconds
import com.sappyoak.konscriptor.core.utils.ticksLong

@OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
class PlatformCoroutineDispatcher(
    private val scheduler: Scheduler,
    private val async: Boolean = false
): CoroutineDispatcher(), Delay {
    private val runLater: (Long, Runnable) -> Task =
        if (async) scheduler.async::runLater else scheduler.sync::runLater

    private val run: (Runnable) -> Task =
        if (async) scheduler.async::run else scheduler.sync::run

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = runLater(timeMillis.milliseconds.ticksLong, Runnable {
            continuation.apply { resumeUndispatched(Unit) }
        })
        continuation.invokeOnCancellation { task.cancel() }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!context.isActive) return
        if (!async && scheduler.isMainThread) block.run() else run(block)
    }
}

fun CoroutineScope.createChildScope(name: String, dispatcher: CoroutineDispatcher = Dispatchers.Sync): CoroutineScope {
    val job = SupervisorJob(parent = coroutineContext.job)
    return CoroutineScope(job + coroutineContext + CoroutineName(name) + dispatcher)
}