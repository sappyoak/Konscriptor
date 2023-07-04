package com.sappyoak.konscriptor.plugin

import org.bukkit.plugin.java.JavaPlugin

import com.sappyoak.konscriptor.common.KonscriptorCore
import com.sappyoak.konscriptor.common.lifecycle.LifecycleAction
import com.sappyoak.konscriptor.common.logging.LoggerFactory

import com.sappyoak.konscriptor.plugin.adapters.BukkitSchedulerAdapter
import com.sappyoak.konscriptor.plugin.adapters.createDelegateLoggerFromPlugin

class KonscriptorPaperPlugin : JavaPlugin() {
    private val schedulerAdapter = BukkitSchedulerAdapter(this)
    private val loggingAdapter = createDelegateLoggerFromPlugin(this)

    private val core = KonscriptorCore(schedulerAdapter, loggingAdapter)

    val logger = LoggerFactory.getLogger(this::class)

    override fun onLoad() {}

    override fun onEnable() {
        core.lifecycleAction(action = LifecycleAction.OnStart)
    }

    override fun onDisable() {
        core.lifecycleAction(action = LifecycleAction.OnShutdown)
    }
}