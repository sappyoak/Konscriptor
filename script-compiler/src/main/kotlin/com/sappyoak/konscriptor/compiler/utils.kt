package com.sappyoak.konscriptor.compiler

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * Thread unsafe version of [lazy] with extra performance.
 *
 * @see LazyThreadSafetyMode.NONE
 */

internal fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * Starts an exposes the given suspending [continuation] as a [Future] value.
 * The [computation] executes synchronously until it's first suspension point like an unconfined dispatch
 * This is an improvement on using runBlocking to connect with the suspending world of the compiler.
 */
internal fun <T> future(context: CoroutineContext = EmptyCoroutineContext, computation: suspend () -> T): Future<T> {
    return FutureContinuation<T>(context).also { completion ->
        computation.startCoroutine(completion)
    }
}

private class FutureContinuation<T>(override val context: CoroutineContext): Future<T>, Continuation<T> {
    private var result: Result<T>? = null
    private val outcomeLatch = CountDownLatch(1)

    override fun resumeWith(result: Result<T>) {
        this.result = result
        outcomeLatch.countDown()
    }

    override fun isCancelled(): Boolean = false
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false
    override fun isDone(): Boolean = result != null

    override fun get(): T {
        outcomeLatch.await()
        return getOrThrow()
    }

    override fun get(timeout: Long, unit: TimeUnit): T {
        return if (outcomeLatch.await(timeout, unit)) getOrThrow()
        else throw TimeoutException()
    }

    private fun getOrThrow() = (result as Result<T>).getOrThrow()
}