package com.softartdev.notedelight

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.compose.rememberNavController
import com.softartdev.notedelight.di.navigationModule
import com.softartdev.notedelight.shared.di.sharedModules
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.util.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import platform.UIKit.UIViewController

class AppHelper : KoinComponent {
    private val router: Router by inject()

    val appUIViewController: UIViewController = ComposeUIViewController {
        val navController = rememberNavController()
        DisposableEffect(key1 = router, key2 = navController) {
            router.setController(navController)
            onDispose(router::releaseController)
        }
        App(navController)
    }

    fun appInit() {
        Napier.base(antilog = DebugAntilog())
        startKoin {
            logger(NapierKoinLogger(Level.DEBUG))
            modules(sharedModules + navigationModule)
        }
    }
}