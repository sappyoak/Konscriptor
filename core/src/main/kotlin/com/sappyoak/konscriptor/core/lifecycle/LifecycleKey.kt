package com.sappyoak.konscriptor.core.lifecycle

@JvmInline
value class LifecycleKey(val value: String)

val RootLifecycleKey = LifecycleKey("RootLifecycle")