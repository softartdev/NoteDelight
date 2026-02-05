package com.softartdev.notedelight.repository

actual object DatabaseFileTransfer {

    actual fun copyDatabase(sourcePath: String, destinationPath: String) {
        throw UnsupportedOperationException("Database import/export is not supported on wasmJs")
    }
}
