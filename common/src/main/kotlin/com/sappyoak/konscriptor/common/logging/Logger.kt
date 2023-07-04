package com.sappyoak.konscriptor.common.logging

typealias MessageProducer = () -> String
typealias EventBuilder = LogEventBuilder.() -> Unit

class Logger(val name: String, private val context: LoggingContext, private val delegate: DelegateLogger) {
    var level: LogLevel = LogLevel.Info

    inline fun verbose(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Verbose)) {
            log(LogLevel.Verbose, block())
        }
    }

    inline fun verbose(block: EventBuilder) {
        if (level.isLoggingEnabled(LogLevel.Verbose)) {
            log(buildLogEvent {
                level = LogLevel.Verbose
                logger = name
                block()
            })
        }
    }

    inline fun debug(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Debug)) {
            log(LogLevel.Debug, block())
        }
    }

    inline fun debug(block: EventBuilder) {
        if (level.isLoggingEnabled(LogLevel.Debug)) {
            log(buildLogEvent {
                level = LogLevel.Debug
                logger = name
                block()
            })
        }
    }

    inline fun info(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Info)) {
            log(LogLevel.Info, block())
        }
    }

    inline fun info(block: EventBuilder) {
        if (level.isLoggingEnabled(LogLevel.Info)) {
            log(buildLogEvent {
                level = LogLevel.Info
                logger = name
                block()
            })
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

    inline fun warn(block: EventBuilder) {
        if (level.isLoggingEnabled(LogLevel.Warn)) {
            log(buildLogEvent {
                level = LogLevel.Warn
                logger = name
                block()
            })
        }
    }

    inline fun error(block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Error)) {
            log(LogLevel.Error, block())
        }
    }

    inline fun error(throwable: Throwable, block: MessageProducer) {
        if (level.isLoggingEnabled(LogLevel.Error)) {
            log(LogLevel.Error, block(), throwable)
        }
    }

    inline fun error(block: EventBuilder) {
        if (level.isLoggingEnabled(LogLevel.Error)) {
            log(buildLogEvent {
                level = LogLevel.Error
                logger = name
                block()
            })
        }
    }

    fun log(passedLevel: LogLevel, passedMessage: String, passedThrowable: Throwable? = null) {
        // format and parse this message into a string
        // call the delegate logger here before we go into coroutine land
        // this way we can sync-log to plugin logger delegate and then async for our own
        // without anything getting screwy
        delegate.log(level, passedMessage, passedThrowable)
        context.consumeEvent(buildLogEvent {
            logger = name
            level = passedLevel
            message = passedMessage
            stackTrace(passedThrowable)
        })
    }

    fun log(event: LogEvent) {
        context.consumeEvent(event)
    }
}