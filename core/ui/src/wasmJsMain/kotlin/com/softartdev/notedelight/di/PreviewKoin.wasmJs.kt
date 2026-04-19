package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import org.koin.core.logger.Level
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

@Composable
actual fun koinPreviewConfiguration(loggerLevel: Level): KoinConfiguration = koinConfiguration {
    printLogger(loggerLevel)
}
