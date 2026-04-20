package com.softartdev.notedelight

import platform.Foundation.NSUUID
import platform.Foundation.NSTemporaryDirectory

actual fun createTempBackupPath(prefix: String): String {
    val tempDir = NSTemporaryDirectory()
    val name = "$prefix-${NSUUID().UUIDString}.db"
    return if (tempDir.endsWith("/")) "$tempDir$name" else "$tempDir/$name"
}
