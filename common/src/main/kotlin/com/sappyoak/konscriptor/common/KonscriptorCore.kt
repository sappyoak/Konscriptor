package com.sappyoak.konscriptor.common

import com.sappyoak.konscriptor.common.concurrent.GlobalContext
import com.sappyoak.konscriptor.common.concurrent.Scheduler
import com.sappyoak.konscriptor.common.lifecycle.LifecycleRegistry
import com.sappyoak.konscriptor.common.logging.DelegateLogger
import com.sappyoak.konscriptor.common.logging.LoggingContext
import com.sappyoak.konscriptor.common.logging.initializeContext

class KonscriptorCore(
    scheduler: Scheduler,
    logger: DelegateLogger
) {
    val context = GlobalContext(scheduler)
    val loggingContext = LoggingContext(logger, context.internalScope)
    val lifecycleRegistry = LifecycleRegistry(context::createChildScope)

    init {
        initializeContext(loggingContext)
    }
}