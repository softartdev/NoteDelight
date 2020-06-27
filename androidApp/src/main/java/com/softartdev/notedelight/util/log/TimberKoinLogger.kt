package com.softartdev.notedelight.util.log

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import timber.log.Timber

class TimberKoinLogger(level: Level) : Logger(level) {

    override fun log(level: Level, msg: MESSAGE) {
        if (this.level <= level) {
            logOnLevel(msg, level)
        }
    }

    private fun logOnLevel(msg: MESSAGE, level: Level) = when (level) {
        Level.DEBUG -> Timber.tag(KOIN_TAG).d(msg)
        Level.INFO -> Timber.tag(KOIN_TAG).i(msg)
        Level.ERROR -> Timber.tag(KOIN_TAG).e(msg)
        else -> Timber.tag(KOIN_TAG).e(msg)
    }
}