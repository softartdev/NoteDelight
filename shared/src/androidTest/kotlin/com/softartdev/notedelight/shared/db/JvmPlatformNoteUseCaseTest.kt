package com.softartdev.notedelight.shared.db

class JvmPlatformNoteUseCaseTest : BasePlatformNoteUseCaseTest() {

    override val platformRepo: PlatformRepo = JvmPlatformRepo()
}