package com.sappyoak.konscriptor.core.lifecycle

import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.CopyOnWriteArraySet

open class Lifecycle(
    val key: LifecycleKey,
    val scope: CoroutineScope
) {
    private val observers: MutableSet<LifecycleObserver> = CopyOnWriteArraySet()

    private val loadOrder: List<LifecycleObserver> get() = observers.sortedByDescending { it.priority }
    private val unloadOrder: List<LifecycleObserver> get() = observers.sortedBy { it.priority }

    fun addObserver(observer: LifecycleObserver) {
        observers.add(observer)
    }

    fun addObserver(priority: Int = 1, consumer: LifecycleEventConsumer) {
        addObserver(LifecycleObserver(priority, consumer))
    }

    inline fun <T : LifecycleEventConsumer> addObserver(priority: Int = 1, block: () -> T): T {
        return block().also { addObserver(priority, it) }
    }

    fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }

    fun clear() {
        observers.clear()
    }

    fun consume(event: LifecycleEvent) {
        val ordered = if (event == LifecycleEvent.OnShutdown) unloadOrder else loadOrder
        for (observer in ordered) observer.consumer(event)
    }
}