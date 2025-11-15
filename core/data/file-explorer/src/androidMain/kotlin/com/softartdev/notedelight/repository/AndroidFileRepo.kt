package com.softartdev.notedelight.repository

import android.content.Context
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class AndroidFileRepo(context: Context) : AbstractFileRepo() {
    override val fileSystem: FileSystem = FileSystem.SYSTEM
    override val zeroPath: Path = context.filesDir.path.toPath()
}

