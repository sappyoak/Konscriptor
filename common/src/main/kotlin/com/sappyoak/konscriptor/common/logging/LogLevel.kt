package com.sappyoak.konscriptor.common.logging

enum class LogLevel(val level: Int) {
    All(Int.MIN_VALUE),
    Verbose(0),
    Debug(20),
    Info(40),
    Warn(80),
    Error(200),
    Off(Int.MAX_VALUE);

    companion object {
        val values: Array<LogLevel> by lazy { values() }
    }
}

fun LogLevel.isLoggingEnabled(other: LogLevel): Boolean = other.level >= level

fun String?.toLogLevel(): LogLevel = when (this) {
    null -> LogLevel.Info
    else -> LogLevel.values.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: LogLevel.Info
}