package com.softartdev.notedelight

import android.app.Application
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.util.NapierKoinLogger
import com.softartdev.notedelight.util.isInLeakCanaryAnalyzerProcess
import com.softartdev.notedelight.util.log.CrashlyticsAntilog
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
            modules(sharedModules + uiModules)
        }
    }
}
