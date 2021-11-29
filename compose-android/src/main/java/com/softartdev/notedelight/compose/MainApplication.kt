package com.softartdev.notedelight.compose

import android.app.Application
import com.softartdev.notedelight.shared.di.allModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Napier.base(antilog = DebugAntilog())
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(allModules)
        }
    }
}