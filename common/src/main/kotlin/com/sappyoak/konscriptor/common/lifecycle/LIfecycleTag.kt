package com.sappyoak.konscriptor.common.lifecycle

/**
 * [LifecycleTag] represents a unique identifier for a [Lifecycle]. Encoding in this identifier is information
 * about where this [Lifecycle] is in the hierarchy of lifecycles decendant from the top-level Konscriptor application
 * lifecycle. This is useful for when we want to have the ability to efficiently post actions to any downstream
 * children of the lifecycle that received the action *if* that child lifecycle accepts events from it's parents.
 *
 * Lifecycles keep an internal representation of any child tags that exist for easy filtering.
 */
@JvmInline
value class LifecycleTag(val value: String = "") {
    val split: List<String> get() = value.split(TAG_DELIMITER)
    val parentTag: LifecycleTag? get() = split.let {
        if (it.size == 1) null
        else it.dropLast(1).joinToString(TAG_DELIMITER).let(::LifecycleTag)
    }

    operator fun get(index: Int): String {
        require(index in split.indices) { "This lifecycle tag does not have that many components making it up. Tag $value only has ${split.size - 1} total parts. Requested one at index: $index" }
        return split[index]
    }

    fun plus(other: LifecycleTag): LifecycleTag = LifecycleTag(value + TAG_DELIMITER + other.value)

    fun contains(other: LifecycleTag): Boolean = value.contains(other.value)

    override fun toString(): String {
        return "LifecycleTag -- value: $value, ${parentTag?.let { "parent: $it" } ?: ""}"
    }

    companion object {
        private const val TAG_DELIMITER = ":"

        val GlobalTag: LifecycleTag = LifecycleTag("Global")
        val ScriptProjectTag: LifecycleTag = LifecycleTag("ScriptProject")
        val DependencyTag: LifecycleTag = LifecycleTag("Dependency")
    }
}

val LifecycleTag.isGlobal: Boolean get() = this == LifecycleTag.GlobalTag
val LifecycleTag.isScriptProject: Boolean get() = this.contains(LifecycleTag.ScriptProjectTag)
val LifecycleTag.isDependency: Boolean get() = this.contains(LifecycleTag.DependencyTag)

inline fun LifecycleTag.executeIfParentExists(block: () -> Unit) {
    if (parentTag != null) block()
}