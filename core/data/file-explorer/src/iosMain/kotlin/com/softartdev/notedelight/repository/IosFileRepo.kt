package com.softartdev.notedelight.repository

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask

class IosFileRepo : AbstractFileRepo() {
    override val fileSystem: FileSystem = FileSystem.SYSTEM

    override val zeroPath: Path by lazy {
        val paths: List<*> = NSSearchPathForDirectoriesInDomains(
            directory = NSApplicationSupportDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        val zeroPath: NSString = paths.first() as NSString
        return@lazy zeroPath.toString().toPath()
    }
}

