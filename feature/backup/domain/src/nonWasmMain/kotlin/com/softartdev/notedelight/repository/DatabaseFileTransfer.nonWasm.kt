package com.softartdev.notedelight.repository

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

actual object DatabaseFileTransfer {

    actual fun copyDatabase(sourcePath: String, destinationPath: String) = FileSystem.SYSTEM
        .copy(source = sourcePath.toPath(), target = destinationPath.toPath())
}
