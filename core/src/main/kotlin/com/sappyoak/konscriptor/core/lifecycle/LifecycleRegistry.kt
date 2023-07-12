package com.sappyoak.konscriptor.core.lifecycle

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap

import com.sappyoak.konscriptor.core.scheduler.Dispatchers
import com.sappyoak.konscriptor.core.scheduler.createChildScope

class LifecycleRegistry(private val scope: CoroutineScope) {
    private val registry: MutableMap<LifecycleKey, Lifecycle> = ConcurrentHashMap()

    init {
        putIfAbsent(RootLifecycleKey)
    }

    val rootLifecycle: Lifecycle get() = registry[RootLifecycleKey]!!

    operator fun get(key: LifecycleKey) = registry[key]

    fun putIfAbsent(key: LifecycleKey) {
        registry.computeIfAbsent(key) {
            Lifecycle(key, scope.createChildScope(key.value))
        }.also { publishLifecycleEvent(LifecycleEvent.OnLifecycleLoad(it)) }
    }

    fun addLifecycle(key: LifecycleKey, parent: Lifecycle? = null, dispatcher: CoroutineDispatcher = Dispatchers.Sync) {
        registry
            .computeIfAbsent(key) {
                Lifecycle(
                    key = key,
                    scope = (parent?.scope ?: scope).createChildScope(key.value, dispatcher)
                )
            }
            .also { publishLifecycleEvent(lifecycleLoad(it)) }

    }

    fun removeLifecycle(key: LifecycleKey) {
        registry.remove(key)?.also {
            publishLifecycleEvent(lifecycleRemove(it))
        }
    }

    fun publishLifecycleEvent(event: LifecycleEvent) {
        for (lifecycle in registry.values) {
            lifecycle.consume(event)
        }
    }
}