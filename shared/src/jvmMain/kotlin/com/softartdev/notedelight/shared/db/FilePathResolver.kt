package com.softartdev.notedelight.shared.db

import com.softartdev.notedelight.MR
import dev.icerock.moko.resources.desc.desc
import net.harawata.appdirs.AppDirs
import net.harawata.appdirs.AppDirsFactory
import java.io.File

class FilePathResolver(
    private val appDirs: AppDirs = AppDirsFactory.getInstance(),
    private val appName: String = MR.strings.app_name.desc().localized(),
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
