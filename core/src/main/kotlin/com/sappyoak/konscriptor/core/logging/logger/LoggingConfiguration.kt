package com.sappyoak.konscriptor.core.logging.logger

import com.sappyoak.konscriptor.core.logging.LogLevel
import com.sappyoak.konscriptor.core.utils.TimeSpan
import com.sappyoak.konscriptor.core.utils.fromDays

const val LOG_FILE_MAX_SIZE = 1028 * 1028 * 25
const val LOG_FILE_MAX_DAYS = 5.0
const val LOG_FILE_MAX_FILE_ENTRIES = 5

data class LoggingConfiguration(
    val level: LogLevel = LogLevel.Info,
    val fileSizeLimit: Int = LOG_FILE_MAX_SIZE,
    val fileAgeLimit: TimeSpan = TimeSpan.fromDays(LOG_FILE_MAX_DAYS),
    val uncompressedFileLimit: Int = LOG_FILE_MAX_FILE_ENTRIES
) {
    inline fun update(block: LoggingConfigurationUpdateBuilder.() -> Unit): LoggingConfiguration {
        return LoggingConfigurationUpdateBuilder(this).apply(block).build()
    }
}


class LoggingConfigurationUpdateBuilder(private val original: LoggingConfiguration) {
    var level: LogLevel? = null
    var fileSizeLimit: Int? = null
    var fileAgeLimit: TimeSpan? = null
    var uncompressedFileLimit: Int? = null

    fun fileAgeLimit(limit: Double) {
        fileAgeLimit = TimeSpan(limit)
    }

    fun build(): LoggingConfiguration = LoggingConfiguration(
        level = level ?: original.level,
        fileSizeLimit = fileSizeLimit ?: original.fileSizeLimit,
        fileAgeLimit = fileAgeLimit ?: original.fileAgeLimit,
        uncompressedFileLimit = uncompressedFileLimit ?: original.uncompressedFileLimit
    )
}