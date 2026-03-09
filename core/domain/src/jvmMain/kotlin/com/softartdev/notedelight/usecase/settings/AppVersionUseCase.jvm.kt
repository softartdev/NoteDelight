package com.softartdev.notedelight.usecase.settings

import co.touchlab.kermit.Logger

actual class AppVersionUseCase {
    private val logger = Logger.withTag("AppVersionUseCase")

    actual operator fun invoke(): String? {
        val pkg: Package? = ClassLoader.getSystemClassLoader().getDefinedPackage("com.softartdev.notedelight")
        logger.i(message = pkg::toString)
        return pkg?.implementationVersion?.takeIf(String::isNotBlank)
    }
}
