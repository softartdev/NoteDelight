package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import org.koin.compose.KoinApplicationPreview
import org.koin.core.logger.Level
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.includes

@Composable
fun PreviewKoin(content: @Composable () -> Unit) {
    val config : KoinConfiguration = koinPreviewConfiguration()
    KoinApplicationPreview(
        application = {
            includes(config)
            modules(sharedModules + uiModules)
        },
        content = {
            val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
                content()
            }
        }
    )
}

@Composable
expect fun koinPreviewConfiguration(loggerLevel: Level = Level.INFO): KoinConfiguration
