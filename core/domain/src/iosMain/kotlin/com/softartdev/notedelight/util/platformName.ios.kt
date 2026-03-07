package com.softartdev.notedelight.util

import platform.Foundation.NSBundle

actual fun platformName(): String = "iOS"

actual fun appVersion(): String {
    return (NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String)
        ?.takeIf { it.isNotBlank() }
        ?: "unknown"
}
