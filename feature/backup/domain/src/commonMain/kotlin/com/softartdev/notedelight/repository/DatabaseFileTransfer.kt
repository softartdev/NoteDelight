package com.softartdev.notedelight.repository

expect object DatabaseFileTransfer {

    fun copyDatabase(sourcePath: String, destinationPath: String)
}
