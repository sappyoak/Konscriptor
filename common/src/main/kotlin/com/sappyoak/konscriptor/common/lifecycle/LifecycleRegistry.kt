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

    fun createAndAddLifecycle(tag: LifecycleTag) {
        val parent = tag.parentTag?.let { lifecycles[it] }
        val name = if (parent == null) tag[0] else tag.split.last()
        lifecycles[tag] = Lifecycle(tag, scopeCreator(name, parent?.scope?.coroutineContext))
        parent?.addChildTag(tag)
    }

    fun removeLifecycle(lifecycle: Lifecycle) {
        lifecycles.remove(lifecycle.tag)
        lifecycle.tag.parentTag?.let { lifecycles[it]?.removeChildTag(lifecycle.tag) }
    }

    fun publishAction(tag: LifecycleTag, action: LifecycleAction) {
        val queue = ArrayDeque<LifecycleTag>().also { it.add(tag) }
        while (queue.isNotEmpty()) {
            val lifecycle: Lifecycle = lifecycles[queue.removeFirst()] ?: continue
            lifecycle.onReceive(action)
            queue.addAll(lifecycle.childTags)
        }
    }
}
