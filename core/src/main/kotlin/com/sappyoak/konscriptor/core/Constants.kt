package com.sappyoak.konscriptor.core

object Constants {
    const val DEFAULT_LOG_BATCH_SIZE = 50
    const val DEFAULT_LOG_EVENT_CHANNEL_SIZE = 250
    const val DEFAULT_LOG_TIMEOUT_MS = 20L

    const val DEFAULT_LOG_FILE_MAX_SIZE = 1028 * 1028 * 25
    const val DEFAULT_LOG_FILE_MAX_DAYS = 5.0
    const val DEFAULT_LOG_FILE_MAX_FILES = 5

    const val INTERNAL_THREAD_POOL_SIZE = 10

    const val ID_DELIMITER = ":"
    const val SCRIPT_EXTENSION = ".kon.kts"

    const val MS_PER_NANOSECOND = (1 / 1000) / 1000
    const val MS_PER_MICROSECOND = 1 / 1000
    const val MS_PER_SECOND = 1_000
    const val MS_PER_MINUTE = 60_000
    const val MS_PER_HOUR = 3_600_000
    const val MS_PER_DAY = 86_400_000
    const val MS_PER_WEEK = 604_800_000

    const val MS_PER_SERVER_TICK = 50.0
}