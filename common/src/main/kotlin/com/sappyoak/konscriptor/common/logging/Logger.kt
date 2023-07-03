package com.sappyoak.konscriptor.common.logging

typealias MessageProducer = () -> String

class Logger(val name: String, private val context: LoggingContext, private val delegate: DelegateLogger) {
    var level: LogLevel = LogLevel.Info

    inline fun verbose(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Verbose)) {
            log(LogLevel.Verbose, block())
        }
    }

    inline fun debug(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Debug)) {
            log(LogLevel.Debug, block())
        }
    }

    inline fun info(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Info)) {
            log(LogLevel.Info, block())
        }
    }

    inline fun warn(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Warn)) {
            log(LogLevel.Warn, block())
        }
    }

    inline fun warn(throwable: Throwable, block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Warn)) {
            log(LogLevel.Warn, block(), throwable)
        }
    }

    inline fun error(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Error)) {
            log(LogLevel.Error, block())
        }
    }

    fun log(level: LogLevel, message: String, throwable: Throwable? = null) {
        // format and parse this message into a string
        // call the delegate logger here before we go into coroutine land
        // this way we can sync-log to plugin logger delegate and then async for our own
        // without anything getting screwy
        delegate.log(level, message, throwable)
        context.consumeEvent(level, message, throwable)
    }
}