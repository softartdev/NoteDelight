package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.router.RouterState

typealias ContentChild = @Composable () -> Unit

interface NoteDelightRoot {

    val routerState: Value<RouterState<*, ContentChild>>
}
