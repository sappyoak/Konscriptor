package com.sappyoak.konscriptor.common.lifecycle

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import java.util.concurrent.ConcurrentHashMap

import com.sappyoak.konscriptor.common.concurrent.ScopeCreator

/**
 * The lifecycle Registry is responsible for creating and coordinating the passing of actions through the
 * hierarchy
 */

class LifecycleRegistry(private val scopeCreator: ScopeCreator) {
    private val lifecycles = ConcurrentHashMap<LifecycleTag, Lifecycle>()

    operator fun get(tag: LifecycleTag): Lifecycle? = lifecycles[tag]

    fun createAndAddLifecycle(tag: LifecycleTag) {
        val parent = tag.parentTag?.let { lifecycles[it] }
        val name = if (parent == null) tag[0] else tag.split.last()
        lifecycles[tag] = Lifecycle(tag, scopeCreator(name, parent?.scope?.coroutineContext))
        parent?.addChildTag(tag)
    }

    fun removeLifecycle(tag: LifecycleTag) {
        lifecycles.remove(tag)
        tag.parentTag?.let { lifecycles[it]?.removeChildTag(tag) }
    }

    fun removeLifecycle(lifecycle: Lifecycle): Unit = removeLifecycle(lifecycle.tag)

    fun publishAction(tag: LifecycleTag = LifecycleTag.GlobalTag, action: LifecycleAction) {
        val queue = ArrayDeque<LifecycleTag>().also { it.add(tag) }
        while (queue.isNotEmpty()) {
            val lifecycle: Lifecycle = lifecycles[queue.removeFirst()] ?: continue
            lifecycle.onReceive(action)
            queue.addAll(lifecycle.childTags)
        }
    }

    fun addObserver(tag: LifecycleTag, priority: Int = 1, observer: LifecycleObserver) {
        lifecycles[tag]?.addObserver(priority, observer)
    }

    fun addObserver(tag: LifecycleTag, observer: LifecycleObserverHandle) {
        lifecycles[tag]?.addObserver(observer)
    }

    fun removeObserver(tag: LifecycleTag, observer: LifecycleObserverHandle) {
        lifecycles[tag]?.removeObserver(observer)
    }
}

val LifecycleRegistry.globalLifecycle: Lifecycle get() = get(LifecycleTag.GlobalTag)!!

fun LifecycleRegistry.addGlobalObserver(priority: Int = 1, observer: LifecycleObserver) {
    globalLifecycle.addObserver(priority, observer)
}

fun LifecycleRegistry.removeGlobalObserver(observer: LifecycleObserverHandle) {
    globalLifecycle.removeObserver(observer)
}

inline fun <T : LifecycleObserver> LifecycleRegistry.addObserver(tag: LifecycleTag, priority: Int = 1, block: () -> T): T {
    return block().also { addObserver(tag, priority, it) }
}


