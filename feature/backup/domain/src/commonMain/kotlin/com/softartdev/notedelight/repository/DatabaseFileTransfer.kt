package com.softartdev.notedelight.repository

expect object DatabaseFileTransfer {

    suspend fun copyDatabase(sourcePath: String, destinationPath: String)
}
