package com.sappyoak.konscriptor.common.concurrent

interface Scheduler {
    val isMainThread: Boolean

    fun cancelTask(taskID: Int)
    fun getPendingTasks(): List<ScheduledTask>

    /**
     * Returns a task that will run on the next server tick
     */
    fun sync(task: Runnable): ScheduledTask

    /** Returns a task that will run after the specified number of ticks **/
    fun syncLater(delay: Long, task: Runnable): ScheduledTask

    /**
     * Returns a task that will repeatedly run after the specified number of
     * ticks until cancelled
     */
    fun syncRepeating(delay: Long, period: Long, task: Runnable): ScheduledTask

    /** Schedules a once off task to occur as soon as possible **/
    fun syncDelayed(task: Runnable): Int
    /** Schedules a once off task to occur after a delay */
    fun syncDelayed(delay: Long, task: Runnable): Int

    fun async(task: Runnable): ScheduledTask
    fun asyncLater(delay: Long, task: Runnable): ScheduledTask
    fun asyncRepeating(delay: Long, period: Long, task: Runnable): ScheduledTask
}