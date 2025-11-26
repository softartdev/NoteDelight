package com.softartdev.notedelight

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.util.DEFAULT_APP_LOG_TAG
import com.softartdev.notedelight.util.kermitLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

class AppHelper : KoinComponent {
    val appUIViewController: UIViewController = ComposeUIViewController { App() }

    fun appInit() {
        Logger.setTag(DEFAULT_APP_LOG_TAG)
        startKoin {
            kermitLogger()
            modules(sharedModules + uiModules)
        }
    }
}
