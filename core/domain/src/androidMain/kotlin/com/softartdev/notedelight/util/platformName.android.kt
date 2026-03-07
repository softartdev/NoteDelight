package com.softartdev.notedelight.util

import android.content.Context

actual fun platformName(): String = "Android"

actual fun appVersion(): String {
    return try {
        val context = Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication")
            .invoke(null) as? Context
        context?.packageManager
            ?.getPackageInfo(context.packageName, 0)
            ?.versionName
            ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}
