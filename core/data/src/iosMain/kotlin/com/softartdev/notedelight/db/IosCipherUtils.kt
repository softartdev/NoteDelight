@file:OptIn(ExperimentalForeignApi::class)

package com.softartdev.notedelight.db

import cnames.structs.sqlite3
import cnames.structs.sqlite3_stmt
import cocoapods.SQLCipher.*
import com.softartdev.notedelight.model.PlatformSQLiteState
import io.github.aakira.napier.Napier
import kotlinx.cinterop.*
import platform.Foundation.*

object IosCipherUtils {

    private val dbDirPath: String by lazy {
        val paths: List<*> = NSSearchPathForDirectoriesInDomains(
            directory = NSApplicationSupportDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        val zeroPath: NSString = paths.first() as NSString
        return@lazy zeroPath.stringByAppendingPathComponent(str = "databases")
    }
    private val nsFileManager = NSFileManager.defaultManager

    fun getDatabaseState(dbName: String): PlatformSQLiteState {
        var result = PlatformSQLiteState.DOES_NOT_EXIST
        val dbPath = getDatabasePath(dbName)
        val dbFileIsExist = nsFileManager.fileExistsAtPath(dbPath)
        Napier.d("db file is exist = $dbFileIsExist, dbPath = $dbPath")
        if (dbFileIsExist) {
            result = PlatformSQLiteState.ENCRYPTED
            memScoped {
                val db = allocPointerTo<sqlite3>()
                val stmt = allocPointerTo<sqlite3_stmt>()
                try {
                    var rc: Int = sqlite3_open(dbPath, db.ptr)
                    checkError(rc, db, "Error opening database")
                    rc = sqlite3_prepare(db.value, "PRAGMA user_version;", -1, stmt.ptr, null)
                    checkError(rc, db, "Error preparing database")
                    rc = sqlite3_step(stmt.value)
                    if (rc == SQLITE_ROW) {
                        result = PlatformSQLiteState.UNENCRYPTED
                        val verPointer = sqlite3_column_text(stmt.value, 0)
                        val version = verPointer?.pointed?.value?.toByte()?.toInt()?.toChar()
                        Napier.d("user_version: $version")
                    } else {
                        checkError(rc, db, "Error retrieving user_version")
                        throw RuntimeException("Error retrieving user_version, result code: $rc")
                    }
                } catch (t: Throwable) {
                    Napier.e(message = t.message ?: "Error while getting database state")
                } finally {
                    sqlite3_finalize(stmt.value)
                    sqlite3_close(db.value)
                }
            }
        }
        return result
    }

    fun checkKey(password: String?, dbName: String): Boolean {
        var result = false
        val dbPath = getDatabasePath(dbName)
        memScoped {
            val db = allocPointerTo<sqlite3>()
            try {
                var rc: Int = sqlite3_open(dbPath, db.ptr)
                checkError(rc, db, "Error opening database")
                val key: CValues<ByteVar>? = password?.cstr
                Napier.d("sqlite3_key key: ${key?.ptr?.toKString()}, rc: $rc, db: $db, dbPath: ${db.value}")
                rc = sqlite3_key(db.value, key?.ptr, key?.size ?: 0)
                checkError(rc, db, "Error key database")
                rc = sqlite3_exec(db.value, "SELECT count(*) FROM sqlite_master;", null, null, null)
                checkError(rc, db, "Error executing database")
                result = rc == SQLITE_OK
            } catch (t: Throwable) {
                Napier.e(message = t.message ?: "Error while checking key")
            } finally {
                sqlite3_close(db.value)
            }
        }
        return result
    }

    fun encrypt(password: String?, dbName: String) {
        val dbPath = getDatabasePath(dbName)
        if (!nsFileManager.fileExistsAtPath(dbPath)) {
            throw RuntimeException("$dbName not found")
        }
        val newDbPath = getDatabasePath("sqlcipherutils.tmp")
        Napier.d("new file path = $newDbPath")
        var newFileIsExist = nsFileManager.fileExistsAtPath(newDbPath)
        if (!newFileIsExist) {
            nsFileManager.createFileAtPath(newDbPath, null, null)
            newFileIsExist = nsFileManager.fileExistsAtPath(newDbPath)
        }
        if (!newFileIsExist) {
            throw RuntimeException("Error creating temporary file")
        }
        memScoped {
            val db = allocPointerTo<sqlite3>()
            val stmt = allocPointerTo<sqlite3_stmt>()
            val version = try {
                // Open the original DB and get the version
                var rc: Int = sqlite3_open(dbPath, db.ptr)
                checkError(rc, db, "Error opening database")
                rc = sqlite3_prepare(db.value, "PRAGMA user_version;", -1, stmt.ptr, null)
                checkError(rc, db, "Error preparing database")
                rc = sqlite3_step(stmt.value)
                if (rc != SQLITE_ROW) {
                    throw RuntimeException("Error retrieving database, result code: $rc")
                }
                val verPointer: CPointer<*>? = sqlite3_column_text(stmt.value, 0)
                requireNotNull(verPointer)
                verPointer.reinterpret<ByteVar>().toKString()
            } finally {
                sqlite3_finalize(stmt.value)
                sqlite3_close(db.value)
            }
            try {
                // Open the new encrypted DB
                var rc: Int = sqlite3_open(newDbPath, db.ptr)
                checkError(rc, db, "Error opening database")
                val key = password?.cstr
                rc = sqlite3_key(db.value, key?.ptr, key?.size ?: 0)
                checkError(rc, db, "Error key database")

                // Attach the original DB to the new encrypted DB
                rc = sqlite3_exec(db.value, "ATTACH DATABASE '$dbPath' AS plaintext KEY ''", null, null, null)
                checkError(rc, db, "Error attaching plaintext database")

                // Export the original DB to the new encrypted DB
                rc = sqlite3_exec(db.value, "SELECT sqlcipher_export('main', 'plaintext')", null, null, null)
                checkError(rc, db, "Error exporting database")

                // Detach the original DB
                rc = sqlite3_exec(db.value, "DETACH DATABASE plaintext", null, null, null)
                checkError(rc, db, "Error detaching plaintext database")

                // Set the version for the new encrypted DB
                rc = sqlite3_exec(db.value, "PRAGMA user_version = $version", null, null, null)
                checkError(rc, db, "Error setting user version")
            } finally {
                sqlite3_close(db.value)
            }
        }
        // Replace the original DB with the new encrypted DB
        nsFileManager.removeItemAtPath(dbPath, null)
        nsFileManager.moveItemAtPath(newDbPath, dbPath, null)
    }

    fun decrypt(password: String?, dbName: String) {
        val dbPath = getDatabasePath(dbName)
        if (!nsFileManager.fileExistsAtPath(dbPath)) {
            throw RuntimeException("$dbName not found")
        }
        val newDbPath = getDatabasePath("sqlcipherutils.tmp")
        Napier.d("new file path = $newDbPath")
        var newFileIsExist = nsFileManager.fileExistsAtPath(newDbPath)
        if (!newFileIsExist) {
            nsFileManager.createFileAtPath(newDbPath, null, null)
            newFileIsExist = nsFileManager.fileExistsAtPath(newDbPath)
        }
        if (!newFileIsExist) {
            throw RuntimeException("Error creating temporary file")
        }
        memScoped {
            val db = allocPointerTo<sqlite3>()
            val stmt = allocPointerTo<sqlite3_stmt>()
            val version = try {
                // Open the encrypted DB and get the version
                var rc: Int = sqlite3_open(dbPath, db.ptr)
                checkError(rc, db, "Error opening database")
                val key = password?.cstr
                rc = sqlite3_key(db.value, key?.ptr, key?.size ?: 0)
                checkError(rc, db, "Error key database")

                // Attach the encrypted DB to the new unencrypted DB
                rc = sqlite3_exec(db.value, "ATTACH DATABASE '$newDbPath' AS plaintext KEY ''", null, null, null)
                checkError(rc, db, "Error attaching plaintext database")

                // Export the encrypted DB to the new decrypted DB
                rc = sqlite3_exec(db.value, "SELECT sqlcipher_export('plaintext')", null, null, null)
                checkError(rc, db, "Error exporting database")

                // Detach the decrypted DB
                rc = sqlite3_exec(db.value, "DETACH DATABASE plaintext", null, null, null)
                checkError(rc, db, "Error detaching plaintext database")

                // Get the version
                rc = sqlite3_prepare(db.value, "PRAGMA user_version;", -1, stmt.ptr, null)
                checkError(rc, db, "Error preparing database")
                rc = sqlite3_step(stmt.value)
                if (rc != SQLITE_ROW) {
                    throw RuntimeException("Error retrieving database, result code: $rc")
                }
                val verPointer: CPointer<*>? = sqlite3_column_text(stmt.value, 0)
                requireNotNull(verPointer)
                verPointer.reinterpret<ByteVar>().toKString()
            } finally {
                sqlite3_finalize(stmt.value)
                sqlite3_close(db.value)
            }
            try {
                // Open the new unencrypted DB
                var rc = sqlite3_open(newDbPath, db.ptr)
                checkError(rc, db, "Error opening database")

                // Set the version for the new unencrypted DB
                rc = sqlite3_exec(db.value, "PRAGMA user_version = $version", null, null, null)
                checkError(rc, db, "Error executing sql")
            } finally {
                sqlite3_close(db.value)
            }
        }
        // Replace the original DB with the new unencrypted DB
        nsFileManager.removeItemAtPath(dbPath, null)
        nsFileManager.moveItemAtPath(newDbPath, dbPath, null)
    }

    fun getDatabasePath(dbName: String): String {
        return NSString.create(string = dbDirPath).stringByAppendingPathComponent(dbName)
    }

    //Visible for tests
    fun deleteDatabase(): Boolean {
        val path = dbDirPath
        Napier.d("db dir exists before = ${nsFileManager.fileExistsAtPath(path)}")
        return nsFileManager.removeItemAtPath(path, null)
    }

    fun checkCipherVersion(dbName: String): String? {
        var result: String? = null
        val dbPath = getDatabasePath(dbName)
        val dbFileIsExist = nsFileManager.fileExistsAtPath(dbPath)
        Napier.d("db file is exist = $dbFileIsExist, dbPath = $dbPath")
        memScoped {
            val db = allocPointerTo<sqlite3>()
            val stmt = allocPointerTo<sqlite3_stmt>()
            try {
//                val password = "correct horse battery staple".cstr
                val password = "".cstr
                var rc: Int = sqlite3_open(dbPath, db.ptr)
                checkError(rc, db, "Error opening database")
                rc = sqlite3_key(db.value, password.ptr, password.size)
//                rc = sqlite3_key_v2(db.value, ":memory:", password.ptr, password.size)
                checkError(rc, db, "Error setting key")
                rc = sqlite3_prepare_v2(db.value, "PRAGMA cipher_version;", -1, stmt.ptr, null)
                checkError(rc, db, "Error preparing SQL")
                rc = sqlite3_step(stmt.value)
                if (rc == SQLITE_ROW) {
                    val uBytePointer: CPointer<UByteVar>? = sqlite3_column_text(stmt.value, 0)
                    val verPointer: CPointer<ByteVar>? = uBytePointer?.reinterpret()
                    result = verPointer?.toKString() ?: ""
                } else {
                    checkError(rc, db, "Error retrieving cipher_version")
                    throw RuntimeException("Error retrieving cipher_version, result code: $rc")
                }
            } finally {
                sqlite3_finalize(stmt.value)
                sqlite3_close(db.value)
            }
        }
        return result
    }

    //TODO: use from lib after update version >= 1.4
    fun checkCipherVersion(): String? {
        var result: String? = null
        memScoped {
            val db = allocPointerTo<sqlite3>()
            val stmt = allocPointerTo<sqlite3_stmt>()
            try {
                val password = "correct horse battery staple".cstr
                var rc: Int = sqlite3_open(":memory:", db.ptr)
                checkError(rc, db, "Error opening database")
                rc = sqlite3_key(db.value, password.ptr, password.size)
//                rc = sqlite3_key_v2(db.value, ":memory:", password.ptr, password.size)
                checkError(rc, db, "Error setting key")
                rc = sqlite3_prepare_v2(db.value, "PRAGMA cipher_version;", -1, stmt.ptr, null)
                checkError(rc, db, "Error preparing SQL")
                rc = sqlite3_step(stmt.value)
                if (rc == SQLITE_ROW) {
                    val uBytePointer: CPointer<UByteVar>? = sqlite3_column_text(stmt.value, 0)
                    val verPointer: CPointer<ByteVar>? = uBytePointer?.reinterpret()
                    result = verPointer?.toKString() ?: ""
                } else {
                    checkError(rc, db, "Error retrieving cipher_version")
                    throw RuntimeException("Error retrieving cipher_version, result code: $rc")
                }
            } finally {
                sqlite3_finalize(stmt.value)
                sqlite3_close(db.value)
            }
        }
        return result
    }

    private fun checkError(rc: Int, db: CPointerVarOf<CPointer<sqlite3>>, title: String) {
        if (rc == SQLITE_OK) return
        val errmsgCPointer: CPointer<ByteVar>? = sqlite3_errmsg(db.value)
        val message: String? = errmsgCPointer?.toKString()
        throw RuntimeException("$title: $message. Result code: $rc")
    }
}