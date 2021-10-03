package com.softartdev.notedelight

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import com.softartdev.notedelight.di.AppModuleImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    Napier.base(antilog = DebugAntilog())

    Window(
        onCloseRequest = ::exitApplication,
        title = "Note Delight",
        state = rememberWindowState(width = 320.dp, height = 480.dp),
        icon = painterResource(resourcePath = "app_icon.png")
    ) {
        Surface {
            MaterialTheme {
                DesktopTheme {
                    RootUi(root()) // Render the Root and its children
                }
            }
        }
    }
}

@Composable
private fun root(): Root =
    // The rememberRootComponent function provides the root ComponentContext and remembers the instance or Root
    rememberRootComponent { componentContext ->
        Root(
            componentContext = componentContext,
            appModule = AppModuleImpl() // Supply dependencies
        )
    }
