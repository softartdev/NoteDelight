package com.softartdev.notedelight.repository

import okio.FileSystem
import okio.Path

class TestFileRepo(
    override val fileSystem: FileSystem,
    override val zeroPath: Path
) : AbstractFileRepo()
