package com.sappyoak.konscriptor.common.lifecycle

/**
 * A marker interface for anything that all Lifecycle actions implement in order to be forwarded
 * to the correct listener
 */
interface LifecycleAction {
    /** Some basic super commonly shared actions that almost all lifecycles implements **/
    companion object {
        val OnStart: LifecycleAction = BasicLifecycleAction("OnStart")
        val OnShutdown: LifecycleAction = BasicLifecycleAction("OnShutdown")
    }
}

@JvmInline
value class BasicLifecycleAction(val type: String) : LifecycleAction

fun interface LifecycleObserver {
    operator fun invoke(action: LifecycleAction)
}

/**
 * A default weighted implementation of the [LifecycleObserver] that delegates to a wrapped observer.
 * The wrapper is weighted and implements the [Comparable] interface for ordereding on the priority
 * in which these observers should be invoked
 */
data class LifecycleObserverHandle(
    val priority: Int = 1,
    val consumer: LifecycleObserver
) : Comparable<LifecycleObserverHandle>, LifecycleObserver by consumer {
    override fun compareTo(other: LifecycleObserverHandle): Int = other.priority.compareTo(priority)
}
