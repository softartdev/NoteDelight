package com.softartdev.notedelight.ui

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import com.softartdev.notedelight.Root
import com.softartdev.notedelight.RootUi
import com.softartdev.notedelight.di.AppModule

@Composable
fun MainRootUI(appModule: AppModule) {
    val darkThemeState: MutableState<Boolean> = remember { mutableStateOf(false) }
    MaterialTheme(colors = if (darkThemeState.value) darkColors() else lightColors()) {
        Surface {
            DesktopTheme {
                RootUi(root = root(appModule, darkThemeState)) // Render the Root and its children
            }
        }
    }
}

@Composable
private fun root(appModule: AppModule, darkThemeState: MutableState<Boolean>): Root =
    // The rememberRootComponent function provides the root ComponentContext and remembers the instance or Root
    rememberRootComponent { componentContext ->
        Root(
            componentContext = componentContext,
            appModule = appModule, // Supply dependencies
            darkThemeState = darkThemeState
        )
    }
