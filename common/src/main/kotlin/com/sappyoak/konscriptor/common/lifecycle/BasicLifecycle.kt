package com.sappyoak.konscriptor.common.lifecycle

import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ConcurrentSkipListSet

/**
 * The basic implementation of a lifecycle. This class is safely open to extended where needed,
 * but most things can rely on this simple implementation and do most of the work in the Observers
 */
open class BasicLifecycle(
    override val tag: LifecycleTag,
    override val scope: CoroutineScope,
    override val acceptsParentActions: Boolean = true
) : Lifecycle {
    private val observers: MutableList<LifecycleObserverHandle> = CopyOnWriteArrayList()
    private var _childTags = ConcurrentSkipListSet<LifecycleTag>()

    private val forwardOrder: List<LifecycleObserver> get() = observers.sortedByDescending { it.priority }
    private val reverseOrder: List<LifecycleObserver> get() = observers.sortedBy { it.priority }

    override val childTags: Set<LifecycleTag> get() = _childTags

    override fun getObservers(): List<LifecycleObserverHandle> = observers.toList()

    override fun onReceive(action: LifecycleAction) {
        val order = if (action == LifecycleAction.OnShutdown) reverseOrder else forwardOrder
        for (observer in order) observer(action)
    }

    override fun addObserver(observer: LifecycleObserverHandle) {
        observers.add(observer)
    }

    override fun removeObserver(observer: LifecycleObserverHandle) {
        observers.remove(observer)
    }

    override fun addChildTag(childTag: LifecycleTag) {
        _childTags.add(childTag)
    }

    override fun removeChildTag(childTag: LifecycleTag) {
        _childTags.remove(childTag)
    }
}