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
        if (dbHolderRef.value != null) {
            return dbHolderRef.value!!
        }
        val passkey = if (passphrase.isEmpty()) null else passphrase.toString()
        dbHolderRef.value = IosDatabaseHolder(
            key = passkey,
            rekey = passkey
        ).freeze()
        return dbHolderRef.value!!
    }

    override fun decrypt(oldPass: CharSequence) {
        closeDatabase()
        dbHolderRef.value = IosDatabaseHolder(
            key = oldPass.toString()
        ).freeze()
    }

    override fun rekey(oldPass: CharSequence, newPass: CharSequence) {
        closeDatabase()
        dbHolderRef.value = IosDatabaseHolder(
            key = oldPass.toString(),
            rekey = newPass.toString()
        ).freeze()
    }

    override fun encrypt(newPass: CharSequence) {
        closeDatabase()
        dbHolderRef.value = IosDatabaseHolder(
            rekey = newPass.toString()
        ).freeze()
    }

    override fun closeDatabase() {
        dbHolderRef.value?.close()
        dbHolderRef.value = null
    }
}