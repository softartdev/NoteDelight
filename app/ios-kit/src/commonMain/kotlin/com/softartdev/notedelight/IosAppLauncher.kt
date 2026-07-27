package com.softartdev.notedelight

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.platformLogWriter
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.util.DEFAULT_APP_LOG_TAG
import com.softartdev.notedelight.util.kermitLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

@OptIn(ExperimentalKermitApi::class)
class IosAppLauncher : KoinComponent {
    val mainViewController: UIViewController by lazy { ComposeUIViewController { App() } }

    fun init(debug: Boolean) {
        Logger.setTag(DEFAULT_APP_LOG_TAG)
        Logger.setLogWriters(kermitLogWriter(debug))
        startKoin {
            kermitLogger()
            modules(sharedModules + uiModules)
        }
    }

    private fun kermitLogWriter(debug: Boolean): LogWriter = when {
        debug -> platformLogWriter()
        else -> {
            setCrashlyticsUnhandledExceptionHook()
            CrashlyticsLogWriter(minSeverity = Severity.Debug)
        }
    }
}
