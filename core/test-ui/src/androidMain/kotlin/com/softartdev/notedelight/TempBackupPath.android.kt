package com.softartdev.notedelight

import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

actual fun createTempBackupPath(prefix: String): String {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    return File.createTempFile(prefix, ".db", context.cacheDir).absolutePath
}
