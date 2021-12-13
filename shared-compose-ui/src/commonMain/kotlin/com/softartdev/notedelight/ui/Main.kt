package com.softartdev.notedelight.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.softartdev.notedelight.Root
import com.softartdev.notedelight.RootUi
import com.softartdev.themepref.PreferableMaterialTheme

@Composable
fun MainRootUI() {
    val root = remember {
        Root(componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()))
    }
    PreferableMaterialTheme {
        Surface {
            RootUi(root) // Render the Root and its children
        }
    }
}
