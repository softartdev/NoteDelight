package com.softartdev.notedelight.db

import com.softartdev.notedelight.repository.SafeRepo
import net.harawata.appdirs.AppDirs
import net.harawata.appdirs.AppDirsFactory
import java.io.File

class FilePathResolver(
    private val appDirs: AppDirs = AppDirsFactory.getInstance(),
    private val appName: String = "Note Delight",
    private val testMode: Boolean = false
) : () -> String {

    override fun invoke(): String {
        val dirPath: String = appDirs.getUserDataDir(appName, null, null)
        val dir = File(dirPath)
        if (!dir.exists() && !testMode) {
            dir.mkdirs()
        }
        val file = File(dir, SafeRepo.DB_NAME)
        return file.absolutePath
    }
}
