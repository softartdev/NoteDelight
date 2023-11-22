@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.softartdev.notedelight.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.softartdev.notedelight.NoteDelightRoot
import com.softartdev.theme.material3.PreferableMaterialTheme

@Composable
fun MainRootUI(component: NoteDelightRoot) {
    PreferableMaterialTheme {
        Surface {
            RootUi(component)
        }
    }
}

@Composable
fun RootUi(component: NoteDelightRoot) {
    Children(stack = component.childStack, animation = stackAnimation(slide()), content = { child ->
        child.instance()
    })
}
