package com.softartdev.notedelight.usecase.settings

import android.content.Context

actual class AppVersionUseCase(private val context: Context) : () -> String? {

    override fun invoke(): String? {
        return context.packageManager?.getPackageInfo(context.packageName, 0)?.versionName
    }
}
