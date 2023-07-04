package com.sappyoak.konscriptor.common.logging

interface DelegateLogger {
    fun getLogger(name: String): DelegateLogger
    fun log(level: LogLevel, message: String) { log(level, message, null) }
    fun log(level: LogLevel, message: String, throwable: Throwable?)
}

