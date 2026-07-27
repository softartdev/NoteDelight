@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.softartdev.notedelight.db

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.throwSQLiteException
import cnames.structs.sqlite3
import cnames.structs.sqlite3_stmt
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_DONE
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_MISUSE
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_NOMEM
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_NULL
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_OK
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_RANGE
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_ROW
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.SQLITE_TRANSIENT
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_bind_blob
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_bind_double
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_bind_int64
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_bind_null
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_bind_text
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_clear_bindings
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_close_v2
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_blob
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_bytes
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_count
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_double
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_int64
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_name
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_text
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_column_type
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_errcode
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_errmsg
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_finalize
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_get_autocommit
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_key
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_open
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_prepare_v2
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_reset
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_step
import swiftPMImport.com.softartdev.notedelight.core.data.db.room.sqlite3_stmt_busy

internal class IosCipherDriver(private val key: String?) : SQLiteDriver {
    override fun open(fileName: String): SQLiteConnection = memScoped {
        val database = allocPointerTo<sqlite3>()
        var resultCode = sqlite3_open(fileName, database.ptr)
        if (resultCode != SQLITE_OK) throwSQLiteException(resultCode, null)

        if (!key.isNullOrEmpty()) {
            val keyBytes = key.cstr
            resultCode = sqlite3_key(database.value, keyBytes.ptr, keyBytes.size - 1)
            if (resultCode != SQLITE_OK) {
                sqlite3_close_v2(database.value)
                throwSQLiteException(resultCode, null)
            }
        }
        IosCipherConnection(requireNotNull(database.value))
    }
}

private class IosCipherConnection(
    private val database: CPointer<sqlite3>,
) : SQLiteConnection {
    private var isClosed = false

    override fun inTransaction(): Boolean {
        checkOpen()
        return sqlite3_get_autocommit(database) == 0
    }

    override fun prepare(sql: String): SQLiteStatement = memScoped {
        checkOpen()
        val statement = allocPointerTo<sqlite3_stmt>()
        val resultCode = sqlite3_prepare_v2(database, sql, -1, statement.ptr, null)
        if (resultCode != SQLITE_OK) throwDatabaseError(resultCode)
        IosCipherStatement(database, requireNotNull(statement.value))
    }

    override fun close() {
        if (!isClosed) {
            isClosed = true
            sqlite3_close_v2(database)
        }
    }

    private fun checkOpen() {
        if (isClosed) throwSQLiteException(SQLITE_MISUSE, "connection is closed")
    }

    private fun throwDatabaseError(resultCode: Int): Nothing =
        throwSQLiteException(resultCode, sqlite3_errmsg(database)?.toKString())
}

private class IosCipherStatement(
    private val database: CPointer<sqlite3>,
    private val statement: CPointer<sqlite3_stmt>,
) : SQLiteStatement {
    private var isClosed = false

    override fun bindBlob(index: Int, value: ByteArray) = checkResult(
        sqlite3_bind_blob(statement, index, value.toCValues(), value.size, SQLITE_TRANSIENT),
    )

    override fun bindDouble(index: Int, value: Double) =
        checkResult(sqlite3_bind_double(statement, index, value))

    override fun bindLong(index: Int, value: Long) =
        checkResult(sqlite3_bind_int64(statement, index, value))

    override fun bindText(index: Int, value: String) =
        checkResult(sqlite3_bind_text(statement, index, value, -1, SQLITE_TRANSIENT))

    override fun bindNull(index: Int) = checkResult(sqlite3_bind_null(statement, index))

    override fun getBlob(index: Int): ByteArray {
        checkRowAndColumn(index)
        val size = sqlite3_column_bytes(statement, index)
        val pointer = sqlite3_column_blob(statement, index)
        return if (pointer == null || size == 0) ByteArray(0) else pointer.readBytes(size)
    }

    override fun getDouble(index: Int): Double {
        checkRowAndColumn(index)
        return sqlite3_column_double(statement, index)
    }

    override fun getLong(index: Int): Long {
        checkRowAndColumn(index)
        return sqlite3_column_int64(statement, index)
    }

    override fun getText(index: Int): String {
        checkRowAndColumn(index)
        val pointer = sqlite3_column_text(statement, index)
        if (pointer == null) throwIfOutOfMemory()
        return requireNotNull(pointer).reinterpret<ByteVar>().toKString()
    }

    override fun isNull(index: Int): Boolean = getColumnType(index) == SQLITE_NULL

    override fun getColumnCount(): Int {
        checkOpen()
        return sqlite3_column_count(statement)
    }

    override fun getColumnName(index: Int): String {
        checkColumn(index)
        return requireNotNull(sqlite3_column_name(statement, index))
            .reinterpret<ByteVar>()
            .toKString()
    }

    override fun getColumnType(index: Int): Int {
        checkRowAndColumn(index)
        return sqlite3_column_type(statement, index)
    }

    override fun step(): Boolean {
        checkOpen()
        return when (val resultCode = sqlite3_step(statement)) {
            SQLITE_ROW -> true
            SQLITE_DONE -> false
            else -> throwDatabaseError(resultCode)
        }
    }

    override fun reset() = checkResult(sqlite3_reset(statement))
    override fun clearBindings() = checkResult(sqlite3_clear_bindings(statement))

    override fun close() {
        if (!isClosed) {
            isClosed = true
            sqlite3_finalize(statement)
        }
    }

    private fun checkResult(resultCode: Int) {
        checkOpen()
        if (resultCode != SQLITE_OK) throwDatabaseError(resultCode)
    }

    private fun checkRowAndColumn(index: Int) {
        checkOpen()
        if (sqlite3_stmt_busy(statement) == 0) {
            throwSQLiteException(SQLITE_MISUSE, "no row")
        }
        checkColumn(index)
    }

    private fun checkColumn(index: Int) {
        if (index !in 0 until getColumnCount()) {
            throwSQLiteException(SQLITE_RANGE, "column index out of range")
        }
    }

    private fun checkOpen() {
        if (isClosed) throwSQLiteException(SQLITE_MISUSE, "statement is closed")
    }

    private fun throwIfOutOfMemory() {
        if (sqlite3_errcode(database) == SQLITE_NOMEM) throw OutOfMemoryError()
    }

    private fun throwDatabaseError(resultCode: Int): Nothing =
        throwSQLiteException(resultCode, sqlite3_errmsg(database)?.toKString())
}
