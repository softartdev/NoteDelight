package com.softartdev.notedelight.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import com.softartdev.notedelight.Root
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.date.toJvmDate
import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

actual object SimpleDateFormat : SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()) {

    actual fun format(input: LocalDateTime): String = format(input.toJvmDate())
}

//TODO remove it
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