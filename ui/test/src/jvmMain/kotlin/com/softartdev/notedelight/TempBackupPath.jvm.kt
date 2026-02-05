package com.softartdev.notedelight

import java.io.File

actual fun createTempBackupPath(prefix: String): String =
    File.createTempFile(prefix, ".db").absolutePath
