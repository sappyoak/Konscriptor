package com.sappyoak.konscriptor.core.scheduler

import org.bukkit.Location
import org.bukkit.entity.Entity

interface Scheduler {
    val async: TaskRunner
    val sync: TaskRunner
    val isMainThread: Boolean

    fun cancelAllTasks()
}

interface Task {
    val isAsync: Boolean
    val isCancelled: Boolean
    val isRepeating: Boolean

    fun cancel()
}

interface TaskRunner {
    fun run(block: Runnable): Task
    fun runLater(delay: Long, block: Runnable): Task
    fun runTimer(delay: Long, period: Long, block: Runnable): Task
    fun runEntity(entity: Entity, block: Runnable, retired: Runnable): Task
    fun runEntityLater(entity: Entity, delay: Long, block: Runnable, retired: Runnable): Task
    fun runEntityTimer(entity: Entity, delay: Long, period: Long, block: Runnable, retired: Runnable): Task
    fun runLocation(location: Location, block: Runnable): Task
    fun runLocationLater(location: Location, delay: Long, block: Runnable): Task
    fun runLocationTimer(location: Location, delay: Long, period: Long, block: Runnable): Task
}