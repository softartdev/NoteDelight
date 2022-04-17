@file:Suppress("EXPERIMENTAL_API_USAGE", "OPT_IN_USAGE")

package com.softartdev.notedelight.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.childAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.slide
import com.softartdev.notedelight.NoteDelightRoot
import com.softartdev.themepref.PreferableMaterialTheme

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
    Children(routerState = component.routerState, animation = childAnimation(slide())) { child ->
        child.instance()
    }
}
