package com.sappyoak.konscriptor.dsl.scheduling

import kotlinx.coroutines.*
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.UUID

import com.sappyoak.konscriptor.dsl.lifecycle.CoreLifecycleListener
import com.sappyoak.konscriptor.dsl.lifecycle.LifecycleRegistry
import com.sappyoak.konscriptor.dsl.lifecycle.getOrPutLifecycle
import com.sappyoak.konscriptor.dsl.platform.Platform


fun Platform<*>.getOrPutCoroutineLifecycle(): LifecycleScope = lifecycleRegistry.getOrPutCoroutineLifecycle(this)
fun LifecycleRegistry.getOrPutCoroutineLifecycle(platform: Platform<*>): LifecycleScope {
    return getOrPutLifecycle(Int.MIN_VALUE) { LifecycleScope(platform) }
}

// Need lifecycle scope manager. Which will probably platform.
class LifecycleScope(
    private val platform: Platform<*>,
    private val parentJob: Job = SupervisorJob()
) : CoreLifecycleListener {
    private val job = SupervisorJob(parentJob)

    val platformCoroutineScope = CoroutineScope(platform.PlatformDispatcher.Sync + job)
    private val playerCoroutineScopes: MutableMap<Player, PlayerCoroutineScope> = hashMapOf()

    override fun onStop() {
        job.cancel()
        for (scope in playerCoroutineScopes.values) {
            scope.cancelJobs()
        }
    }

    fun getPlayerScope(player: Player): CoroutineScope {
        return playerCoroutineScopes[player]?.scope ?: createAndAddPlayerScope(player).scope
    }

    private fun createAndAddPlayerScope(player: Player): PlayerCoroutineScope {
        return PlayerCoroutineScope(job, platform.PlatformDispatcher.Sync, player).also { playerCoroutineScopes[player] = it }
    }
}

class PlayerCoroutineScope(
    private val parentJob: Job,
    private val dispatcher: CoroutineDispatcher,
    private val player: Player
) {
    private val job = SupervisorJob(parentJob)
    val scope = CoroutineScope(dispatcher + CoroutineName("PlayerCoroutine:${player.name}") + job)

    fun cancelJobs() = job.cancel()
}