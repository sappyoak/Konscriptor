package com.sappyoak.konscriptor.dsl.lifecycle

import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap

class LifecycleRegistry(globalCoroutineScope: CoroutineScope) {
    val lifecycleScope = LifecycleCoroutineScope(globalCoroutineScope)

    init {
        registerLifecycle(DefaultLifecycle(LifecycleTag.GlobalTag))
        globalLifecycle.addEventHandle(Int.MIN_VALUE, lifecycleScope)
    }

    private val registry: MutableMap<LifecycleTag, Lifecycle> = ConcurrentHashMap()

    val size: Int get() = registry.size
    val isEmpty: Boolean get() = registry.isEmpty()
    val keys: Set<LifecycleTag> get() = registry.keys
    val entries: Set<Map.Entry<LifecycleTag, Lifecycle>> get() = registry.entries.toSet()

    val globalLifecycle: Lifecycle get() = registry[LifecycleTag.GlobalTag]!!

    operator fun get(tag: LifecycleTag) = registry[tag]
    operator fun get(tag: String) = registry[LifecycleTag(tag)]
    operator fun set(tag: LifecycleTag, lifecycle: Lifecycle) = registry.putIfAbsent(tag, lifecycle)

    fun registerLifecycle(lifecycle: Lifecycle) {
        set(lifecycle.tag, lifecycle)
    }

    fun registerProjectLifecycle(name: String) {
        registerLifecycle(DefaultLifecycle(LifecycleTag.ProjectTag.plus(name)))
    }

    fun unregisterLifecycle(tag: LifecycleTag) {
        registry.remove(tag)
    }

    fun unregisterLifecycle(lifecycle: Lifecycle) {
        registry.remove(lifecycle.tag)
    }
}

fun LifecycleRegistry.getOrThrow(tag: LifecycleTag): Lifecycle {
    return get(tag) ?: throw RuntimeException("No lifecycle registered for $tag")
}

@Suppress("UNCHECKED_CAST")
inline fun <T : Lifecycle> LifecycleRegistry.getOrElse(tag: LifecycleTag, block: () -> T): T {
    return get(tag)?.let { it as T } ?: block()
}