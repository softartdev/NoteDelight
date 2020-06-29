package com.softartdev.notedelight.shared.db

class IosPlatformNoteUseCaseTest : BasePlatformNoteUseCaseTest() {

    override val platformRepo: PlatformRepo = IosPlatformRepo()
}