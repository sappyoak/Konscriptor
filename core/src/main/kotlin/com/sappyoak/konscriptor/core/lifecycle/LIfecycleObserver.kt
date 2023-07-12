package com.sappyoak.konscriptor.core.lifecycle

@JvmInline
value class LifecycleObservableProp(val prop: String) : LifecycleEvent

interface LifecycleEvent {
    data class OnLifecycleLoad(val lifecycle: Lifecycle) : LifecycleEvent
    data class OnLifecycleRemove(val lifecycle: Lifecycle) : LifecycleEvent

    companion object {
        val OnEnable: LifecycleEvent = LifecycleObservableProp("OnEnable")
        val OnLoad: LifecycleEvent = LifecycleObservableProp("OnLoad")
        val OnShutdown: LifecycleEvent = LifecycleObservableProp("OnShutdown")
        val OnReload: LifecycleEvent = LifecycleObservableProp("OnReload")
    }
}

fun lifecycleLoad(lifecycle: Lifecycle) = LifecycleEvent.OnLifecycleLoad(lifecycle)
fun lifecycleRemove(lifecycle: Lifecycle) = LifecycleEvent.OnLifecycleRemove(lifecycle)

typealias LifecycleEventConsumer = (LifecycleEvent) -> Unit

data class LifecycleObserver(
    val priority: Int = 1,
    val consumer: LifecycleEventConsumer
): Comparable<LifecycleObserver> {
    override fun compareTo(other: LifecycleObserver): Int = other.priority.compareTo(priority)
}
