package com.softartdev.notedelight.usecase.settings

import co.touchlab.kermit.Logger
import java.util.Properties

actual class AppVersionUseCase {
    private val logger = Logger.withTag("AppVersionUseCase")

    actual operator fun invoke(): String? {
        val classLoader = javaClass.classLoader ?: ClassLoader.getSystemClassLoader()
        val properties = Properties()
        runCatching { classLoader.getResourceAsStream("version.properties").use(properties::load) }
            .onSuccess { logger.i { "Loaded properties: $properties" } }
            .onFailure { logger.e(it) { "Failed to load version.properties" } }
        return properties.getProperty("version")
    }
}
