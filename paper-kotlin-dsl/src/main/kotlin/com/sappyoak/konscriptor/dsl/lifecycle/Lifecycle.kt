package com.sappyoak.konscriptor.dsl.lifecycle

import java.util.concurrent.CopyOnWriteArrayList

interface Lifecycle {
    val tag: LifecycleTag

    fun addEventHandle(handle: LifecycleEventHandle)
    fun addEventHandle(priority: Int = 1, consumer: LifecycleEventConsumer) {
        addEventHandle(LifecycleEventHandle(consumer, priority, tag))
    }

    fun removeEventHandle(handle: LifecycleEventHandle)
    fun clear()

    fun publishEvent(event: LifecycleEvent)
    fun handlers(): List<LifecycleEventHandle>
}

inline fun <reified T : LifecycleEventListener> Lifecycle.getOrElse(priority: Int = 1, factory: () -> T): T {
    return handlers().map { it.consumer }
        .filterIsInstance<T>()
        .firstOrNull()
        ?: factory().also { addEventHandle(priority, it) }
}

open class DefaultLifecycle(override val tag: LifecycleTag) : Lifecycle {
    protected val handlers: MutableList<LifecycleEventHandle> = CopyOnWriteArrayList()
    protected val forwardOrder: List<LifecycleEventHandle> get() = handlers.sortedByDescending { it.priority }
    protected val reverseOrder: List<LifecycleEventHandle> get() = handlers.sortedBy { it.priority }

    override fun handlers() = handlers
    override fun addEventHandle(handle: LifecycleEventHandle) {
        handlers.add(handle)
    }

    override fun removeEventHandle(handle: LifecycleEventHandle) {
        handlers.remove(handle)
    }

    override fun clear() {
        handlers.clear()
    }

    override fun publishEvent(event: LifecycleEvent) {
        val order = if (event is LifecycleEvent.Shutdown) reverseOrder else forwardOrder
        for (handle in order) {
            if (event is LifecycleEvent.Resume) handle.unpause()
            if (!handle.isPaused) handle.consumer(event)
            if (event is LifecycleEvent.Pause) handle.pause()
        }
    }
}