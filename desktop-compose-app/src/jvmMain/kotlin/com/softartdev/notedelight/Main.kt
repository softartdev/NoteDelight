package com.softartdev.notedelight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.compose.rememberNavController
import com.softartdev.notedelight.di.navigationModule
import com.softartdev.notedelight.shared.di.sharedModules
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.util.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.app_icon
import notedelight.shared_compose_ui.generated.resources.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.get

fun main() {
    Napier.base(antilog = DebugAntilog())
    startKoin {
        logger(NapierKoinLogger(Level.DEBUG))
        modules(sharedModules + navigationModule)
    }
    val router: Router = get(Router::class.java)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 320.dp, height = 480.dp),
            icon = painterResource(Res.drawable.app_icon)
        ) {
            CustomDesktopTheme {
                val navController = rememberNavController()
                router.setController(navController)
                App(navController)
            }
        }
    }
}
