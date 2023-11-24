@file:OptIn(ExperimentalDecomposeApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.shared.runOnUiThread
import com.softartdev.notedelight.shared.util.NapierKoinLogger
import com.softartdev.notedelight.ui.MainRootUI
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun main() {
    Napier.base(antilog = DebugAntilog())
    startKoin {
        logger(NapierKoinLogger(Level.DEBUG))
        modules(allModules)
    }
    val lifecycle = LifecycleRegistry()
    val root = runOnUiThread {
        RootComponent(componentContext = DefaultComponentContext(lifecycle))
    }
    application {
        val windowState = rememberWindowState(width = 320.dp, height = 480.dp)
        LifecycleController(lifecycle, windowState)

        Window(
            onCloseRequest = ::exitApplication,
            title = MR.strings.app_name.desc().localized(),
            state = windowState,
            icon = painterResource(resourcePath = "app_icon.png")
        ) {
            CustomDesktopTheme {
                MainRootUI(root)
            }
        }
    }
}
