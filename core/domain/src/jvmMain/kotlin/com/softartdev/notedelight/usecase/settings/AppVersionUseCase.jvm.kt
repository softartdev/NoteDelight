package com.softartdev.notedelight.usecase.settings

import co.touchlab.kermit.Logger
import java.util.Properties

actual class AppVersionUseCase {
    private val logger = Logger.withTag("AppVersionUseCase")

    actual operator fun invoke(): String? {
        val fromManifest = runCatching {
            val classLoader = AppVersionUseCase::class.java.classLoader ?: ClassLoader.getSystemClassLoader()
            classLoader.getDefinedPackage("com.softartdev.notedelight")?.implementationVersion
        }.getOrNull()?.takeIf(String::isNotBlank)
        if (fromManifest != null) return fromManifest

        val fromProperties = runCatching {
            (AppVersionUseCase::class.java.classLoader ?: ClassLoader.getSystemClassLoader())
                .getResourceAsStream("version.properties")?.use { stream ->
                    Properties().apply { load(stream) }.getProperty("version")?.trim()
                }
        }.getOrNull()?.takeIf(String::isNotBlank)
        if (fromProperties != null) return fromProperties

        logger.w { "App version not found in manifest or version.properties" }
        return null
    }
}
