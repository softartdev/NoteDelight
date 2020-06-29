package com.softartdev.notedelight.shared.db

expect class PlatformRepo {

    var noteDb: NoteDb?

    val dbState: PlatformSQLiteState

    val noteQueries: NoteQueries

    var relaunchFlowEmitter: (() -> Unit)?

    fun buildDatabaseInstanceIfNeed(passphrase: CharSequence = ""): NoteDb

    fun decrypt(oldPass: CharSequence)

    fun rekey(oldPass: CharSequence, newPass: CharSequence)

    fun encrypt(newPass: CharSequence)

    fun closeDatabase()
}