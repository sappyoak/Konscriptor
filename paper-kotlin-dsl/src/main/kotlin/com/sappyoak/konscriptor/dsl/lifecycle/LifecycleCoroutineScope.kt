package com.sappyoak.konscriptor.dsl.lifecycle

import org.bukkit.entity.Player
import kotlin.coroutines.*
import kotlinx.coroutines.*

import com.sappyoak.konscriptor.dsl.platform.player.OnlinePlayerMap

/**
 * A Wrapper around a coroutine scope that extends from the parent scope.
 * It ensures that all lifecycle tasks will be executed on the main Thread.
 *
 * The scope ensures that any tasks will be canceled when the plugin disables,
 * or when a player disconnects any scopes associated with that player will be removed
 */
class LifecycleCoroutineScope(
    private val parentScope: CoroutineScope
): GlobalLifecycleListener {
    private val job = SupervisorJob()
    private val playerCoroutineScope by lazy {
        OnlinePlayerMap<PlayerCoroutineScope>()
    }

    val lifecycleScope = CoroutineScope(parentScope.coroutineContext + job + CoroutineName("LifecycleCoroutineScope"))

    override fun onShutdown(event: LifecycleEvent.Shutdown) {
        job.cancel()
        for (scope in playerCoroutineScope.values) {
            scope.cancelJobs()
        }
    }

    fun getPlayerCoroutineScope(player: Player): CoroutineScope {
        return playerCoroutineScope[player]?.scope
            ?: PlayerCoroutineScope(lifecycleScope, player.name).also {
                playerCoroutineScope.put(player, it) { scope ->
                    scope.cancelJobs()
                }
            }.scope
    }
}

class PlayerCoroutineScope(parentScope: CoroutineScope, playerName: String) {
    val job = SupervisorJob()
    val scope = CoroutineScope(parentScope.coroutineContext + job + CoroutineName("PlayerCoroutineScope-$playerName"))

    fun cancelJobs() = job.cancel()
}
