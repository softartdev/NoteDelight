package com.softartdev.notedelight

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.ui.MainScreen

class NoteDelightRootPreview : NoteDelightRoot {

    override val childStack: Value<ChildStack<*, ContentChild>> = MutableValue(
        initialValue = ChildStack(
            configuration = Configuration.Main,
            instance = {
                MainScreen(
                    mainViewModel = getViewModel(),
                    onItemClicked = {},
                    onSettingsClick = {}
                )
            }
        )
    )
}