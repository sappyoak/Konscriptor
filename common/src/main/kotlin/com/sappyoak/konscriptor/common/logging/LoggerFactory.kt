package com.sappyoak.konscriptor.common.logging

import kotlin.reflect.KClass

private lateinit var loggingContext: LoggingContext

fun initializeContext(context: LoggingContext) {
    if (::loggingContext.isInitialized.not()) {
        loggingContext = context
    }
}

object LoggerFactory {
    fun getLogger(name: String) = loggingContext.getLogger(name)
    fun getLogger(klass: KClass<*>) = loggingContext.getLogger(klass)
}