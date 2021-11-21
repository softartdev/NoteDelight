package com.softartdev.notedelight.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.softartdev.notedelight.Root
import com.softartdev.notedelight.RootUi
import com.softartdev.notedelight.di.AppModule

@Composable
fun MainRootUI(appModule: AppModule) {
    val darkThemeState: MutableState<Boolean> = remember { mutableStateOf(false) }

    MaterialTheme(colors = if (darkThemeState.value) darkColors() else lightColors()) {
        Surface {
            RootUi(
                root = Root(
                    componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
                    appModule = appModule, // Supply dependencies
                    darkThemeState = darkThemeState
                )
            ) // Render the Root and its children
        }
    }
}
