package com.softartdev.notedelight.db

import com.softartdev.notedelight.model.PlatformSQLiteState
import io.toxicity.sqlite.mc.driver.SQLiteMCDriver
import io.toxicity.sqlite.mc.driver.config.DatabasesDir
import io.toxicity.sqlite.mc.driver.config.encryption.Key
import java.io.File

object JvmCipherUtils {
    private val dbFile = File(FilePathResolver().invoke())

    private val factory = SQLiteMCDriver.Factory(
        dbName = dbFile.name,
        schema = NoteDb.Schema
    ) {
        filesystem(DatabasesDir(dbFile.parentFile!!)) {
            encryption {
                sqlCipher { v4() }
            }
        }
    }

    fun getDatabaseState(): PlatformSQLiteState {
        if (!dbFile.exists()) return PlatformSQLiteState.DOES_NOT_EXIST
        return try {
            factory.createBlocking(Key.Empty).close()
            PlatformSQLiteState.UNENCRYPTED
        } catch (_: Throwable) {
            PlatformSQLiteState.ENCRYPTED
        }
    }

    fun createDriver(passphrase: CharSequence = "", rekey: CharSequence? = null): SQLiteMCDriver {
        val key = if (passphrase.isEmpty()) Key.Empty else Key.passphrase(passphrase.toString())
        return if (rekey != null) {
            val rekeyKey = if (rekey.isEmpty()) Key.Empty else Key.passphrase(rekey.toString())
            factory.createBlocking(key, rekeyKey)
        } else {
            factory.createBlocking(key)
        }
    }
}
