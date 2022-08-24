package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

typealias ContentChild = @Composable () -> Unit

interface NoteDelightRoot {

    val childStack: Value<ChildStack<*, ContentChild>>
}
