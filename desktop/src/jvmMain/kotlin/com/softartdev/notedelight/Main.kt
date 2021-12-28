package com.softartdev.notedelight

import androidx.compose.desktop.DesktopTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.ui.MainRootUI
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun main() = application {
    Napier.base(antilog = DebugAntilog())
    startKoin {
        printLogger(level = Level.ERROR) // TODO revert to Level.DEBUG after update Koin version above 3.1.4
        modules(allModules)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = MR.strings.app_name.desc().localized(),
        state = rememberWindowState(width = 320.dp, height = 480.dp),
        icon = painterResource(resourcePath = "app_icon.png")
    ) {
        DesktopTheme {
            MainRootUI()
        }
    }
}
