package com.softartdev.notedelight.compose

import android.app.Application
import com.softartdev.mr.MokoResHolder
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.shared.util.isInLeakCanaryAnalyzerProcess
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (isInLeakCanaryAnalyzerProcess) return
        Napier.base(antilog = CustomAntilog())
        startKoin {
            androidLogger(level = Level.ERROR) // TODO revert to Level.DEBUG after update Koin version above 3.1.5
            androidContext(this@MainApplication)
            modules(allModules)
        }
        MokoResHolder.init(applicationContext)
    }
}