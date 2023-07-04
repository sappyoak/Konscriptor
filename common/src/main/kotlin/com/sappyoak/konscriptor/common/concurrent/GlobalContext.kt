package com.sappyoak.konscriptor.common.concurrent

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import com.sappyoak.konscriptor.common.INTERNAL_THREAD_POOL_SIZE
import com.sappyoak.konscriptor.common.lifecycle.LifecycleAction
import com.sappyoak.konscriptor.common.lifecycle.LifecycleObserver


typealias ScopeCreator = (name: String, parentContext: CoroutineContext?) -> CoroutineScope

class GlobalContext(platformScheduler: Scheduler) : LifecycleObserver {
    private val threadCount = AtomicInteger(0)

    private val internalThreadPool = Executors.newFixedThreadPool(INTERNAL_THREAD_POOL_SIZE) { runnable ->
        Thread(runnable, "konscriptor-${threadCount.incrementAndGet()}")
    }

    init {
        initializeDispatchers(platformScheduler, internalThreadPool)
    }

    private val rootScope = CoroutineScope(SupervisorJob() + CoroutineName("KonscriptorRoot"))
    private val rootContext = rootScope.coroutineContext

    val globalScope = CoroutineScope(
        SupervisorJob(parent = rootContext.job) +
               rootContext +
               CoroutineName("KonscriptorGlobalScope") +
               Dispatchers.Sync
    )

    val internalScope = CoroutineScope(
        SupervisorJob(parent = rootContext.job) +
               rootContext +
               CoroutineName("KonscriptorInternalScope") +
               Dispatchers.Internal
    )

    override fun invoke(action: LifecycleAction) {
        if (action == LifecycleAction.OnShutdown) {
            close()
        }
    }

    /** Close the root scope and let structured concurrency work it's magic and cancel all of the child tasks **/
    fun close() {
        rootScope.cancel()
    }

    fun createChildScope(name: String, parentContext: CoroutineContext? = globalScope.coroutineContext): CoroutineScope {
        return createArbitraryChildScope(name, parentContext!!, Dispatchers.Internal)
    }

    fun createInternalChildScope(name: String, parentContext: CoroutineContext = internalScope.coroutineContext): CoroutineScope {
        return createArbitraryChildScope(name, parentContext, Dispatchers.Internal)
    }

    private fun createArbitraryChildScope(
        name: String,
        parentContext: CoroutineContext,
        dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        val job = SupervisorJob(parent = parentContext.job)
        return CoroutineScope(job + parentContext + CoroutineName(name) + dispatcher)
    }
}