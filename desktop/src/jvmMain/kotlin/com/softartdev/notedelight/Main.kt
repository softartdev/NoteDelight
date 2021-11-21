package com.softartdev.notedelight

import androidx.compose.desktop.DesktopTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.di.AppModuleImpl
import com.softartdev.notedelight.ui.MainRootUI
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    Napier.base(antilog = DebugAntilog())
    val appModule = AppModuleImpl()

    Window(
        onCloseRequest = ::exitApplication,
        title = MR.strings.app_name.desc().localized(),
        state = rememberWindowState(width = 320.dp, height = 480.dp),
        icon = painterResource(resourcePath = "app_icon.png")
    ) {
        DesktopTheme {
            MainRootUI(appModule)
        }
    }
}
