package com.softartdev.notedelight.repository

import net.harawata.appdirs.AppDirsFactory
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

class JvmFileRepo : AbstractFileRepo() {

    override val fileSystem: FileSystem = FileSystem.SYSTEM

    override val zeroPath: Path = AppDirsFactory.getInstance()
            .getUserDataDir("Note Delight", null, null)
            .let(::File)
            .absolutePath
            .toPath()
}

