package com.softartdev.notedelight.usecase.settings

actual class AppVersionUseCase {

    actual operator fun invoke(): String? {
        return javaClass.`package`?.implementationVersion?.takeIf(String::isNotBlank)
    }
}
