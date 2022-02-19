package com.softartdev.notedelight

import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.ui.MainScreen

class NoteDelightRootPreview : NoteDelightRoot {

    override val routerState: Value<RouterState<*, ContentChild>> = MutableValue(
        initialValue = RouterState(
            configuration = Configuration.Main,
            instance = {
                MainScreen(
                    mainViewModel = getViewModel(),
                    onItemClicked = {},
                    onSettingsClick = {},
                )
            }
        )
    )
}