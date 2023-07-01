package com.sappyoak.konscriptor.dsl.lifecycle

import java.util.concurrent.atomic.AtomicBoolean

typealias LifecycleEventConsumer = (LifecycleEvent) -> Unit

sealed interface LifecycleListener : LifecycleEventConsumer {
    val tag: LifecycleTag
    val isPaused: Boolean
    val acceptsParentEvents: Boolean

    override operator fun invoke(event: LifecycleEvent) { notify(event) }
    fun notify(event: LifecycleEvent)

    fun pause() {}
    fun unpause() {}
}

interface GlobalListenersInterface : LifecycleListener {
    fun onShutdown(event: LifecycleEvent.Shutdown) {}
    fun onInitialize(event: LifecycleEvent.Initialize) {}
    fun onStart(event: LifecycleEvent.Start) {}
    fun onPause(event: LifecycleEvent.Pause) {}
    fun onReload(event: LifecycleEvent.Reload) {}
    fun onResume(event: LifecycleEvent.Resume) {}
    fun <T : Any> onConfigChange(event: LifecycleEvent.ConfigChange<T>) {}
}

interface ProjectListenersInterface : GlobalListenersInterface {
    fun onRecompile(event: LifecycleEvent.Recompile) {}
    fun onCorrupted(event: LifecycleEvent.Corrupted) {}
    fun onScriptLoad(event: LifecycleEvent.ScriptLoad) {}
    fun onScriptUnload(event: LifecycleEvent.ScriptUnload) {}
    fun onScriptDependencyLoad(event: LifecycleEvent.ScriptDependencyLoad) {}

    fun onParentEvent(event: LifecycleEvent) {}
}

abstract class AbstractLifecycleListener : GlobalListenersInterface {
    abstract override val tag: LifecycleTag
    abstract override val acceptsParentEvents: Boolean

    private val paused = AtomicBoolean()
    override val isPaused: Boolean = paused.get()

    override fun pause() { paused.compareAndSet(false, true) }
    override fun unpause() { paused.compareAndSet(true, false) }

    fun canConsumeEvent(event: LifecycleEvent): Boolean {
        if (isPaused && event !is LifecycleEvent.Resume) return false
        if (!acceptsParentEvents && isTagAncestor(event.tag)) return false
        return true
    }

    protected fun isTagAncestor(eventTag: LifecycleTag): Boolean {
        if (tag.startsWith(eventTag[0])) {
            if (!acceptsParentEvents) error("The lifecycle listener tagged $tag does not support receiving ancestor events. " )
            else return true
        }
        return false
    }

}

open class GlobalLifecycleListener : AbstractLifecycleListener() {
    override val tag: LifecycleTag = LifecycleTag.GlobalTag
    override val acceptsParentEvents: Boolean = false

    override fun notify(event: LifecycleEvent) {
        if (!canConsumeEvent(event)) return
        when (event) {
            is LifecycleEvent.Shutdown -> onShutdown(event)
            is LifecycleEvent.Initialize -> onInitialize(event)
            is LifecycleEvent.Start -> onStart(event)
            is LifecycleEvent.Pause -> onPause(event)
            is LifecycleEvent.Reload -> onReload(event)
            is LifecycleEvent.Resume -> onResume(event)
            is LifecycleEvent.ConfigChange<*> -> onConfigChange(event)
            else -> generateNotifyInvalidTypeErr("GlobalLifecycleListener", event)
        }
    }
}

open class ProjectLifecycleListener(name: String) : AbstractLifecycleListener(), ProjectListenersInterface {
    override val tag: LifecycleTag = LifecycleTag.ProjectTag.plus(name)
    override val acceptsParentEvents = true

    override fun notify(event: LifecycleEvent) {
        if (!canConsumeEvent(event)) return
        if (isTagAncestor(event.tag) && acceptsParentEvents) {
            onParentEvent(event)
            return
        }

        when (event) {
            is LifecycleEvent.Shutdown -> onShutdown(event)
            is LifecycleEvent.Initialize -> onInitialize(event)
            is LifecycleEvent.Start -> onStart(event)
            is LifecycleEvent.Pause -> onPause(event)
            is LifecycleEvent.Reload -> onReload(event)
            is LifecycleEvent.Resume -> onResume(event)
            is LifecycleEvent.ConfigChange<*> -> onConfigChange(event)
            is LifecycleEvent.Recompile -> onRecompile(event)
            is LifecycleEvent.Corrupted -> onCorrupted(event)
            is LifecycleEvent.ScriptLoad -> onScriptLoad(event)
            is LifecycleEvent.ScriptUnload -> onScriptUnload(event)
            is LifecycleEvent.ScriptDependencyLoad -> onScriptDependencyLoad(event)
        }
    }
}

private fun generateNotifyInvalidTypeErr(listenerType: String, event: LifecycleEvent): String {
   throw IllegalArgumentException("This event $event is not accepted by $listenerType. Try publishing this to a lifecycle that has a tag similar to ${event.tag}")
}

data class LifecycleEventHandle(
    val event: LifecycleEvent,
    val consumer: LifecycleEventConsumer,
    val priority: Int = 1,
    val tag: LifecycleTag,
) : Comparable<LifecycleEventHandle> {
    override fun compareTo(other: LifecycleEventHandle): Int = other.priority.compareTo(priority)
}
