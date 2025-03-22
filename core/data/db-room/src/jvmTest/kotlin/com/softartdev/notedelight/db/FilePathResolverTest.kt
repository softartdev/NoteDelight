package com.softartdev.notedelight.db

import com.softartdev.notedelight.repository.SafeRepo
import net.harawata.appdirs.impl.MacOSXAppDirs
import net.harawata.appdirs.impl.UnixAppDirs
import net.harawata.appdirs.impl.WindowsAppDirs
import net.harawata.appdirs.impl.WindowsFolderResolver
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathResolverTest : WindowsFolderResolver {
    private val home = System.getProperty("user.home")
    private val appName = "Note Delight"

    @Test
    fun macOSXAppDirs() = assertEquals(
        expected = "$home/Library/Application Support/$appName/${SafeRepo.DB_NAME}",
        actual = FilePathResolver(MacOSXAppDirs(), testMode = true).invoke()
    )

    @Test
    fun windowsAppDirs() = assertEquals(
        expected = "$home/AppData/Local/$appName/${SafeRepo.DB_NAME}",
        actual = FilePathResolver(WindowsAppDirs(this), testMode = true).invoke()
    )

    @Test
    fun unixAppDirs() = assertEquals(
        expected = "$home/.local/share/$appName/${SafeRepo.DB_NAME}",
        actual = FilePathResolver(UnixAppDirs(), testMode = true).invoke()
    )

    override fun resolveFolder(folderId: WindowsAppDirs.FolderId?): String = when (folderId) {
        WindowsAppDirs.FolderId.LOCAL_APPDATA -> "$home/AppData/Local"
        else -> error("Unsupported folder ID: $folderId")
    }
}