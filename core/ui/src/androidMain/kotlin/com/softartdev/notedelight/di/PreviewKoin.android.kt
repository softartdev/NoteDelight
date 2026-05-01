package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.logger.Level
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

@Composable
actual fun koinPreviewConfiguration(loggerLevel: Level): KoinConfiguration {
    val appContext = LocalContext.current.applicationContext ?: error("Android ApplicationContext not found in current Compose context!")
    return koinConfiguration {
        androidContext(appContext)
        printLogger(loggerLevel)
    }
}
