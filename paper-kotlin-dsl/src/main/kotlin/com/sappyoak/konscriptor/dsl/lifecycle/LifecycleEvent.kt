package com.sappyoak.konscriptor.dsl.lifecycle

import java.io.File

sealed class LifecycleEvent(open val tag: LifecycleTag) {
    data class Shutdown(override val tag: LifecycleTag, val time: Long, val reason: ShutdownReason = ShutdownReason.Unknown) : LifecycleEvent(tag)
    data class Initialize(override val tag: LifecycleTag, val time: Long) : LifecycleEvent(tag)
    data class Start(override val tag: LifecycleTag, val time: Long) : LifecycleEvent(tag)
    data class Reload(override val tag: LifecycleTag) : LifecycleEvent(tag)
    data class Pause(override val tag: LifecycleTag, val time: Long, val reason: PauseReason = PauseReason.Unknown) : LifecycleEvent(tag)
    data class Resume(override val tag: LifecycleTag, val time: Long) : LifecycleEvent(tag)
    data class ConfigChange<T : Any>(override val tag: LifecycleTag, val time: Long, val prev: T? = null, val next: T? = null) : LifecycleEvent(tag)
    data class Recompile(override val tag: LifecycleTag, val time: Long) : LifecycleEvent(tag)
    data class Corrupted(override val tag: LifecycleTag, val time: Long, val file: File) : LifecycleEvent(tag)
    data class ScriptLoad(override val tag: LifecycleTag, val time: Long, val file: File) : LifecycleEvent(tag)
    data class ScriptUnload(override val tag: LifecycleTag, val time: Long, val file: File, val reason: UnloadReason = UnloadReason.Unknown) : LifecycleEvent(tag)
    data class ScriptDependencyLoad(override val tag: LifecycleTag, val time: Long, val scriptName: String, val dependencies: Set<File> = hashSetOf()) : LifecycleEvent(tag)
}

@JvmInline
value class ShutdownReason(val reason: String) {
    companion object {
        val Unknown: ShutdownReason = ShutdownReason("unknown")
    }
}

@JvmInline
value class PauseReason(val reason: String) {
    companion object {
        val Unknown: PauseReason = PauseReason("unknown")
    }
}

@JvmInline
value class UnloadReason(val reason: String) {
    companion object {
        val Unknown: UnloadReason = UnloadReason("unknown")
    }
}
