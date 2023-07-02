package com.sappyoak.konscriptor.dsl.lifecycle

@JvmInline
value class LifecycleTag(private val tag: String = "") {
    fun plus(other: LifecycleTag): LifecycleTag = LifecycleTag(tag + ":" + other.tag)
    fun plus(other: String): LifecycleTag = LifecycleTag("$tag:$other")

    operator fun component1(): String = tag.split(TAG_DELIMITER)[0]
    operator fun component2(): String = tag.split(TAG_DELIMITER)[1]

    operator fun get(index: Int): String {
        require(index in 0 until 2) { "A lifecycle tag is only composed of 2 parts. There is no part $index"  }
        return if (index == 0) component1() else component2()
    }

    fun startsWith(str: String): Boolean {
        return tag.startsWith(tag)
    }

    fun startsWith(other: LifecycleTag): Boolean {
        return tag.startsWith(other.tag)
    }

    companion object {
        private const val TAG_DELIMITER = ":"

        val GlobalTag: LifecycleTag = LifecycleTag("Global")
        val ProjectTag: LifecycleTag = LifecycleTag("Project")
    }
}

val LifecycleTag.isGlobalTag: Boolean get() = this == LifecycleTag.GlobalTag
val LifecycleTag.isProjectTag: Boolean get() = this.startsWith(LifecycleTag.ProjectTag)
