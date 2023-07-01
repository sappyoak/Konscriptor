package com.sappyoak.konscriptor.dsl.scheduling

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

val Plugin.PlatformDispatcher: PlatformDispatcher get() = PlatformDispatcher(this as JavaPlugin)

class PlatformDispatcher(private val plugin: JavaPlugin) {
    val Async: BukkitDispatcher by lazy { BukkitDispatcher(plugin, true) }
    val Sync: BukkitDispatcher by lazy { BukkitDispatcher(plugin, false) }
}

private val bukkitScheduler get() = Bukkit.getScheduler()

@OptIn(InternalCoroutinesApi::class)
class BukkitDispatcher(
    val plugin: JavaPlugin,
    val async: Boolean = false
) : CoroutineDispatcher(), Delay {
    private val runTaskLater: (Plugin, Runnable, Long) -> BukkitTask =
        if (async) bukkitScheduler::runTaskLaterAsynchronously
        else bukkitScheduler::runTaskLater

    private val runTask: (Plugin, Runnable) -> BukkitTask =
        if (async) bukkitScheduler::runTaskAsynchronously
        else bukkitScheduler::runTask

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = runTaskLater(plugin, Runnable {
            continuation.apply { resumeUndispatched(Unit) }
        }, timeMillis / 50)
        continuation.invokeOnCancellation { task.cancel() }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!context.isActive) {
            return
        }

        if (!async && Bukkit.isPrimaryThread()) block.run()
        else runTask(plugin, block)
    }
}

interface DispatchQueue {
    fun canRun(): Boolean
    fun dispatchAndEnqueue(context: CoroutineContext, block: Runnable)
}

class PausingDispatcher(private val plugin: JavaPlugin, private val queue: DispatchQueue) : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        if (!Bukkit.isPrimaryThread()) return true
        return !queue.canRun()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.dispatchAndEnqueue(context, block)
    }
}

class ClosableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}