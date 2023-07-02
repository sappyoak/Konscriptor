package com.sappyoak.konscriptor.dsl.lifecycle

interface LifecycleOwner {
    val lifecycle: Lifecycle
}

open class DefaultLifecycleOwner(
    override val lifecycle: Lifecycle
) : LifecycleOwner, Lifecycle by lifecycle

