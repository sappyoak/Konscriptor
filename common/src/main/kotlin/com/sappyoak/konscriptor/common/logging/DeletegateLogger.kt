package com.sappyoak.konscriptor.common.logging

import java.util.logging.Level
import java.util.logging.Logger as JLogger

fun interface DelegateProvider {
    fun get(): DelegateLogger
}

interface DelegateLogger {
    fun getLogger(name: String): DelegateLogger
    fun log(level: LogLevel, message: String, throwable: Throwable? = null)
}

class JavaDelegateLogger(private val root: JLogger) : DelegateLogger {
    override fun getLogger(name: String) = JavaDelegateLogger(JLogger.getLogger(name))

    fun log(level: LogLevel, message: String) {
        log(level, message, null)
    }

    override fun log(level: LogLevel, message: String, throwable: Throwable?) {
        root.log(level.toJavaLevel(), message, throwable)
    }

    private fun LogLevel.toJavaLevel(): Level = when (this) {
        LogLevel.All -> Level.ALL
        LogLevel.Verbose -> Level.FINER
        LogLevel.Debug -> Level.FINE
        LogLevel.Info -> Level.INFO
        LogLevel.Warn -> Level.WARNING
        LogLevel.Error -> Level.SEVERE
        LogLevel.Off -> Level.OFF
    }
}
