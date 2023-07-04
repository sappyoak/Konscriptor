package com.sappyoak.konscriptor.common

import com.sappyoak.konscriptor.common.concurrent.GlobalContext
import com.sappyoak.konscriptor.common.concurrent.Scheduler
import com.sappyoak.konscriptor.common.lifecycle.*
import com.sappyoak.konscriptor.common.logging.DelegateLogger
import com.sappyoak.konscriptor.common.logging.LoggingContext
import com.sappyoak.konscriptor.common.logging.initializeContext

class KonscriptorCore(
    scheduler: Scheduler,
    logger: DelegateLogger
) {
    private val context = GlobalContext(scheduler)
    private val loggingContext = LoggingContext(logger, context.internalScope)
    private val lifecycleRegistry = LifecycleRegistry(context::createChildScope)

    init {
        initializeContext(loggingContext)
        lifecycleRegistry.createAndAddLifecycle(LifecycleTag.GlobalTag)
        lifecycleRegistry.addGlobalObserver(Int.MIN_VALUE, context)
    }

    val logger = loggingContext.getLogger("KonscriptorCore")

    fun lifecycleAction(tag: LifecycleTag = LifecycleTag.GlobalTag, action: LifecycleAction) {
        lifecycleRegistry.publishAction(tag, action)
    }

    fun getLifecycle(tag: LifecycleTag): Lifecycle? = lifecycleRegistry[tag]

    fun addLifecycle(tag: LifecycleTag) {
        lifecycleRegistry.createAndAddLifecycle(tag)
    }

    fun removeLifecycle(tag: LifecycleTag) {
        lifecycleRegistry.removeLifecycle(tag)
    }
}