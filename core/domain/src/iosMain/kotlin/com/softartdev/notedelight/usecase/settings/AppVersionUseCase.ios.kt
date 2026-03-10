package com.softartdev.notedelight.usecase.settings

import platform.Foundation.NSBundle

actual class AppVersionUseCase {

    actual operator fun invoke(): String? {
        val ver = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
        return ver?.takeIf(String::isNotBlank)
    }
}
