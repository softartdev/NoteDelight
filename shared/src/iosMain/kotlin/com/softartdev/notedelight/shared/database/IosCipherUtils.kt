package com.softartdev.notedelight.shared.database

import cnames.structs.sqlite3
import cnames.structs.sqlite3_stmt
import cocoapods.SQLCipher.*
import kotlinx.cinterop.*
import platform.Foundation.*

@Suppress("CAST_NEVER_SUCCEEDS")
@OptIn(ExperimentalUnsignedTypes::class)
object IosCipherUtils {

    private val dbDirPath: NSString by lazy {
        val paths: List<*> = NSSearchPathForDirectoriesInDomains(
            directory = NSApplicationSupportDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        val zeroPath: NSString = paths.first() as NSString
        return@lazy zeroPath.stringByAppendingPathComponent(str = "databases") as NSString
    }
    private val nsFileManager = NSFileManager.defaultManager

    fun getDatabaseState(dbName: String): PlatformSQLiteState {
        var result = PlatformSQLiteState.DOES_NOT_EXIST
        val dbPath = getDatabasePath(dbName)
        val dbFileIsExist = nsFileManager.fileExistsAtPath(dbPath)
        println("db file is exist = $dbFileIsExist")
        if (dbFileIsExist) memScoped {
            result = PlatformSQLiteState.ENCRYPTED
            val db = allocPointerTo<sqlite3>()
            var rc: Int = sqlite3_open(dbPath, db.ptr)
            if (rc != SQLITE_OK) printError("Error opening database", db)
            val stmt = allocPointerTo<sqlite3_stmt>()
            rc = sqlite3_prepare(db.value, "PRAGMA user_version;", -1, stmt.ptr, null)
            if (rc != SQLITE_OK) printError("Error preparing database", db)
            rc = sqlite3_step(stmt.value)
            if (rc == SQLITE_ROW) {
                result = PlatformSQLiteState.UNENCRYPTED
                val verPointer = sqlite3_column_text(stmt.value, 0)
                val version = verPointer?.pointed?.value?.toByte()?.toChar()
                println("user_version: $version")
            } else printError("Error retrieving database", db)
            sqlite3_finalize(stmt.value)
            sqlite3_close(db.value)
        }
        return result
    }

    fun checkKey(password: String?, dbName: String): Boolean {
        var result = false
        memScoped {
            val dbPath = getDatabasePath(dbName)
            val db = allocPointerTo<sqlite3>()
            var rc: Int = sqlite3_open(dbPath, db.ptr)
            if (rc != SQLITE_OK) printError("Error opening database", db)
            val key = password?.cstr
            rc = sqlite3_key(db.value, key?.ptr, key?.size ?: 0)
//            rc = sqlite3_key_v2(db.value, dbName, key?.ptr, key?.size ?: 0)
            if (rc != SQLITE_OK) printError("Error key database", db)
            rc = sqlite3_exec(db.value, "SELECT count(*) FROM sqlite_master;", null, null, null)
            if (rc != SQLITE_OK) printError("Error executing database", db)
            result = rc == SQLITE_OK
            sqlite3_close(db.value)
        }
        return result
    }

    private fun printError(title: String = "Error", db: CPointerVarOf<CPointer<sqlite3>>) {
        val errmsg = sqlite3_errmsg(db.value)
        println("$title: ${errmsg?.toKString()}")
    }

    fun getDatabasePath(dbName: String): String = dbDirPath.stringByAppendingPathComponent(dbName)

    //Visible for tests
    fun deleteDatabase(): Boolean {
        val path = dbDirPath.toString()
        println("db dir exists before = ${nsFileManager.fileExistsAtPath(path)}")
        return nsFileManager.removeItemAtPath(path, null)
    }
}