package com.softartdev.notedelight

import androidx.compose.ui.window.ComposeUIViewController
import com.softartdev.notedelight.di.navigationModule
import com.softartdev.notedelight.shared.di.sharedModules
import com.softartdev.notedelight.shared.util.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import platform.UIKit.UIViewController

class AppHelper {
    val appUIViewController: UIViewController = ComposeUIViewController { App() }

    fun appInit() {
        Napier.base(antilog = DebugAntilog())
        startKoin {
            logger(NapierKoinLogger(Level.DEBUG))
            modules(sharedModules + navigationModule)
        }
    }
}