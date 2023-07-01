package com.sappyoak.konscriptor.dsl.lifecycle

import java.util.concurrent.CopyOnWriteArrayList

interface Lifecycle {
    fun notify(event: LifecycleEvent)
    fun addListener(listener: LifecycleEventHandle)
    fun removeListener(Listener: LifecycleEventHandle)
    fun clear()

    fun onShutdown(event: LifecycleEvent) {}
    fun onInitialize(event: LifecycleEvent) {}
    fun onStart(event: LifecycleEvent) {}
    fun onReload(event: LifecycleEvent) {}
    fun onConfigChange(event: LifecycleEvent) {}
    fun onParentEvent(event: LifecycleEvent) {}
}

abstract class AbstractLifecycle : Lifecycle {
    // @TODO: Consider making this a non-locking concurrent-linked-list
    private val listeners: MutableList<LifecycleEventHandle> = CopyOnWriteArrayList()

    protected val forwardOrder: List<LifecycleEventHandle> get() = listeners.sortedByDescending { it.priority }
    protected val reverseOrder: List<LifecycleEventHandle> get() = listeners.sortedBy { it.priority }
    protected open val acceptsParentEvents: Boolean = false

    abstract val baseTag: LifecycleTag

    override fun addListener(listener: LifecycleEventHandle) {
        listeners.add(listener)
    }

    override fun removeListener(listener: LifecycleEventHandle) {
        listeners.remove(listener)
    }

    override fun clear() {
        // should shutdown anything that could potentially be running in this scope..
        listeners.clear()
    }
}





