package com.softartdev.notedelight

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import com.softartdev.notedelight.di.AppModuleImpl
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    Napier.base(antilog = DebugAntilog())

    Window(
        onCloseRequest = ::exitApplication,
        title = MR.strings.app_name.desc().localized(),
        state = rememberWindowState(width = 320.dp, height = 480.dp),
        icon = painterResource(resourcePath = "app_icon.png")
    ) {
        val darkThemeState: MutableState<Boolean> = remember { mutableStateOf(false) }
        MaterialTheme(colors = if (darkThemeState.value) darkColors() else lightColors()) {
            Surface {
                DesktopTheme {
                    RootUi(root(darkThemeState)) // Render the Root and its children
                }
            }
        }
    }
}

@Composable
private fun root(darkThemeState: MutableState<Boolean>): Root =
    // The rememberRootComponent function provides the root ComponentContext and remembers the instance or Root
    rememberRootComponent { componentContext ->
        Root(
            componentContext = componentContext,
            appModule = AppModuleImpl(), // Supply dependencies
            darkThemeState = darkThemeState
        )
    }
