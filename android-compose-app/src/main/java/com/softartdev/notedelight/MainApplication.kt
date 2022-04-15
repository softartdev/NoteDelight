package com.softartdev.notedelight

import android.app.Application
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.shared.util.isInLeakCanaryAnalyzerProcess
import com.softartdev.notedelight.shared.util.log.CrashlyticsAntilog
import com.softartdev.notedelight.shared.util.log.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (isInLeakCanaryAnalyzerProcess) return
        Napier.base(antilog = if (BuildConfig.DEBUG) DebugAntilog() else CrashlyticsAntilog())
        startKoin {
            logger(NapierKoinLogger(Level.DEBUG))
            androidContext(this@MainApplication)
            modules(allModules)
        }
    }
}