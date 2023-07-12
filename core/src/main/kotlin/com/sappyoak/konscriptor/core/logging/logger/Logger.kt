package com.sappyoak.konscriptor.core.logging.logger

import com.sappyoak.konscriptor.core.logging.LoggingContext
import com.sappyoak.konscriptor.core.logging.LogLevel
import com.sappyoak.konscriptor.core.logging.isLoggingEnabled
import com.sappyoak.konscriptor.core.utils.TimeSpan

@JvmInline
value class LoggerName(val value: String)


class Logger(
    val name: LoggerName,
    private var config: LoggingConfiguration,
    private val context: LoggingContext,
    private val platformSender: PlatformSender = PlatformSender(),
) : ComponentReceiverHolder by platformSender {

    val level: LogLevel get() = config.level
    val fileSizeLimit: Int get() = config.fileSizeLimit
    val fileAgeLimit: TimeSpan get() = config.fileAgeLimit
    val uncompressedFileLimit: Int get() = config.uncompressedFileLimit

    fun updateConfig(block: LoggingConfigurationUpdateBuilder.() -> Unit) {
        config = config.update(block)
    }

    fun setLevel(newLevel: LogLevel) {
        updateConfig { level = newLevel }
    }

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

    fun log(passedLevel: LogLevel, passedMessage: String, passedThrowable: Throwable? = null) {}
    fun log(event: LogEvent) {}
}