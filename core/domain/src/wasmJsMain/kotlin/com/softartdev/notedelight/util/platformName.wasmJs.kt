package com.softartdev.notedelight.util

import kotlinx.browser.document

actual fun platformName(): String = "wasmJs"

actual fun appVersion(): String {
    return try {
        document.querySelector("meta[name=\"app-version\"]")
            ?.getAttribute("content")
            ?.takeIf { it.isNotBlank() }
            ?: "dev"
    } catch (e: Throwable) {
        "dev"
    }
}