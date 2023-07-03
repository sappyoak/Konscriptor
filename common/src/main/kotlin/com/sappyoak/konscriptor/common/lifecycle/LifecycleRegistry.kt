package com.sappyoak.konscriptor.common.lifecycle

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import java.util.concurrent.ConcurrentHashMap

/**
 * The lifecycle Registry is responsible for creating and coordinating the passing of actions through the
 * hierarchy
 */

class LifecycleRegistry(private val rootContext: CoroutineContext) {
    private val lifecycles = ConcurrentHashMap<LifecycleTag, Lifecycle>()

    fun createAndAddLifecycle(tag: LifecycleTag) {
        val parent = tag.parentTag?.let { lifecycles[it] }
        val name = if (parent == null) tag[0] else tag.split.last()
        val lifecycle = Lifecycle(tag, createLifecycleScope(name, parent?.scope?.coroutineContext ?: rootContext))
        lifecycles[tag] = lifecycle
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

    private fun createLifecycleScope(name: String, context: CoroutineContext): CoroutineScope {
        val job = SupervisorJob(parent = context.job)
        return CoroutineScope(job + context + CoroutineName(name))
    }
}
