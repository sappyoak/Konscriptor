package com.sappyoak.konscriptor.common.concurrent

interface ScheduledTask {
    val id: Int
    val isSync: Boolean

    fun cancel()
}

