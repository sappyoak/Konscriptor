package com.sappyoak.konscriptor.dsl.scheduling

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import java.util.concurrent.ConcurrentHashMap

internal data class TimeLimitValue(val startTime: Long, val timeTaken: Long) {
    val isTimeExceeded: Boolean get() = System.currentTimeMillis() - startTime > timeTaken
}

fun interface TaskExecutor {
    fun task(duration: Long, block: () -> Unit): Task

    interface Task {
        val isCancelled: Boolean
        fun cancel()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ExecutionTimeLimiter(private val executor: TaskExecutor, private val rootContext: CoroutineContext) {
    private val contextsLimited = ConcurrentHashMap<CoroutineContext, TimeLimitValue>()

    suspend fun limitTimePerTick(time: Duration, passedContext: CoroutineContext = rootContext) {
        val limitValue = contextsLimited[passedContext]
        if (limitValue == null) {
            registerContextTimeLimit(passedContext, time)
        } else {
            if (limitValue.isTimeExceeded) {
                unregisterContextTimeLimit(passedContext)
                suspendCancellableCoroutine<Unit> { continuation ->
                    val runnable = executor.task(1) { continuation.resume(Unit, null) }
                    continuation.invokeOnCancellation {
                        if (runnable.isCancelled.not()) runnable.cancel()
                    }
                }
            }
        }
    }
    fun registerContextTimeLimit(context: CoroutineContext, time: Duration) {
        contextsLimited[context] = TimeLimitValue(System.currentTimeMillis(), time.inWholeMilliseconds)
    }

    fun unregisterContextTimeLimit(context: CoroutineContext) {
        contextsLimited.remove(context)
    }
}
