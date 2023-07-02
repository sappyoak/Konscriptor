package com.sappyoak.konscriptor.dsl.lifecycle

typealias LifecycleEventConsumer = (LifecycleEvent) -> Unit

interface LifecycleEventListener : LifecycleEventConsumer {
    fun onShutdown(event: LifecycleEvent.Shutdown) {}
    fun onInitialize(event: LifecycleEvent.Initialize) {}
    fun onStart(event: LifecycleEvent.Start) {}
    fun onPause(event: LifecycleEvent.Pause) {}
    fun onReload(event: LifecycleEvent.Reload) {}
    fun onResume(event: LifecycleEvent.Resume) {}
    fun <T : Any> onConfigChange(event: LifecycleEvent.ConfigChange<T>) {}

    override operator fun invoke(event: LifecycleEvent) { onReceiveEvent(event) }

    fun onReceiveEvent(event: LifecycleEvent)
}

interface GlobalLifecycleListener : LifecycleEventListener {
    override fun onReceiveEvent(event: LifecycleEvent) {
        when (event) {
            is LifecycleEvent.Shutdown -> onShutdown(event)
            is LifecycleEvent.Initialize -> onInitialize(event)
            is LifecycleEvent.Start -> onStart(event)
            is LifecycleEvent.Pause -> onPause(event)
            is LifecycleEvent.Reload -> onReload(event)
            is LifecycleEvent.Resume -> onResume(event)
            is LifecycleEvent.ConfigChange<*> -> onConfigChange(event)
            else -> {}
        }
    }
}

data class LifecycleEventHandle(
    val consumer: LifecycleEventConsumer,
    val priority: Int = 1,
    val tag: LifecycleTag
) : Comparable<LifecycleEventHandle> {
    private var paused = false
    val isPaused: Boolean get() = paused

    fun pause() { paused = true }
    fun unpause() { paused = false }

    override fun compareTo(other: LifecycleEventHandle): Int = other.priority.compareTo(priority)
}