package com.softartdev.notedelight

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.notedelight.util.DEFAULT_APP_LOG_TAG
import com.softartdev.notedelight.util.kermitLogger
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin

fun main() {
    Logger.setTag(DEFAULT_APP_LOG_TAG)
    startKoin {
        kermitLogger()
        modules(sharedModules + uiModules)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 1024.dp, height = 768.dp),
            icon = rememberVectorPainter(image = Icons.Filled.FileLock),
        ) {
            CustomDesktopTheme {
                App()
            }
        }
    }
}
