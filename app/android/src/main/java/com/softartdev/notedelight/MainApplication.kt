@file:OptIn(ExperimentalKermitApi::class)

package com.softartdev.notedelight

import android.app.Application
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.platformLogWriter
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.util.DEFAULT_APP_LOG_TAG
import com.softartdev.notedelight.util.kermitLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.setTag(DEFAULT_APP_LOG_TAG)
        Logger.setLogWriters(if (BuildConfig.DEBUG) platformLogWriter() else CrashlyticsLogWriter(minSeverity = Severity.Debug))
        startKoin {
            kermitLogger()
            androidContext(this@MainApplication)
            modules(sharedModules + uiModules)
        }
    }
}
