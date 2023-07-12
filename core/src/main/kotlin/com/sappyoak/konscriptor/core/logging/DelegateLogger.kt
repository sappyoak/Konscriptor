package com.sappyoak.konscriptor.core.logging

/**
 * A small interface for the logging instance provided by the platform. Should generally not be used
 * externally
 */

interface DelegateLogger {
    fun getLogger(name: String): DelegateLogger
    fun log(level: LogLevel, message: String) { log(level, message, null) }
    fun log(level: LogLevel, message: String, throwable: Throwable?)
}