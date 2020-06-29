package com.softartdev.notedelight.shared.db

import kotlinx.coroutines.test.runBlockingTest

class JvmPlatformNoteUseCaseTest : BasePlatformNoteUseCaseTest() {

    override val platformRepo: PlatformRepo = JvmPlatformRepo()

    override fun <T> runTest(block: suspend () -> T) {
        runBlockingTest { block() }
    }
}