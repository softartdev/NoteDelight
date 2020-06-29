package com.softartdev.notedelight.shared.db

import kotlinx.coroutines.runBlocking

class IosPlatformNoteUseCaseTest : BasePlatformNoteUseCaseTest() {

    override val platformRepo: PlatformRepo = IosPlatformRepo()

    override fun <T> runTest(block: suspend () -> T) {
        runBlocking { block() }
    }
}