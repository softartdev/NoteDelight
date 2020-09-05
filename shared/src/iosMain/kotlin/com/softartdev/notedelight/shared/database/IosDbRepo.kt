package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.data.PlatformSQLiteThrowable
import com.softartdev.notedelight.shared.db.NoteQueries
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

class IosDbRepo : DatabaseRepo() {

    private val dbHolderRef: AtomicReference<DatabaseHolder?> = AtomicReference(buildDatabaseInstanceIfNeed())

    override val databaseState: PlatformSQLiteState
        get() = TODO("Not yet implemented")

    override val noteQueries: NoteQueries
        get() = dbHolderRef.value?.noteQueries ?: throw PlatformSQLiteThrowable("DB is null")

    override fun buildDatabaseInstanceIfNeed(passphrase: CharSequence): DatabaseHolder {
        dbHolderRef.value = IosDatabaseHolder().freeze()
        return dbHolderRef.value!!
    }

    override fun decrypt(oldPass: CharSequence) {
        TODO("Not yet implemented")
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    override fun encrypt(newPass: CharSequence) {
        TODO("Not yet implemented")
    }

    override fun closeDatabase() {
        dbHolderRef.value?.close()
        dbHolderRef.value = null
    }
}