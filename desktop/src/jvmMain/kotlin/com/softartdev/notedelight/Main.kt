package com.softartdev.notedelight

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.di.AppModuleImpl
import com.softartdev.notedelight.ui.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    Napier.base(antilog = DebugAntilog())

    val appModule: AppModule = AppModuleImpl()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Note Delight",
        state = rememberWindowState(width = 320.dp, height = 480.dp),
        icon = painterResource(resourcePath = "app_icon.png")
    ) {
        App(appModule)
    }
}
