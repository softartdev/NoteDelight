package com.softartdev.notedelight.util

actual fun platformName(): String = "Java"

actual fun appVersion(): String {
    return try {
        listOf(
            "com.softartdev.notedelight",
            "com.softartdev.notedelight.util",
        ).mapNotNull { pkg ->
            Package.getPackage(pkg)?.implementationVersion
        }.firstOrNull() ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}
