@file:OptIn(ExperimentalDecomposeApi::class)

package com.softartdev.notedelight

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
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.app_icon
import notedelight.shared_compose_ui.generated.resources.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
            title = stringResource(Res.string.app_name),
            state = windowState,
            icon = painterResource(Res.drawable.app_icon)
        ) {
            CustomDesktopTheme {
                MainRootUI(root)
            }
        }
    }
}
