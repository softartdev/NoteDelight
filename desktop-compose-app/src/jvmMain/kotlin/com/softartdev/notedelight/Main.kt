package com.softartdev.notedelight

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.notedelight.util.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.get

fun main() {
    Napier.base(antilog = DebugAntilog())
    startKoin {
        logger(NapierKoinLogger(Level.DEBUG))
        modules(sharedModules + uiModules)
    }
    val router: Router = get(Router::class.java)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 480.dp, height = 640.dp),
            icon = rememberVectorPainter(image = Icons.Filled.FileLock),
        ) {
            CustomDesktopTheme {
                App(router)
            }
        }
    }
}
