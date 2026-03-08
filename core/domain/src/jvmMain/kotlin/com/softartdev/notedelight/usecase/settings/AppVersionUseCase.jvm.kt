package com.softartdev.notedelight.usecase.settings

actual class AppVersionUseCase : () -> String? {

    override fun invoke(): String? {
        val pack: Package? = Package.getPackage("com.softartdev.notedelight")
        return pack?.implementationVersion
    }
}