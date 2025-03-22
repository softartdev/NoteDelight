package com.softartdev.notedelight.db

import androidx.room.RoomRawQuery
import androidx.room.paging.LimitOffsetPagingSource
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.softartdev.notedelight.model.Note
import kotlinx.datetime.LocalDateTime

class ModelLimitOffsetPagingSource(
    sourceQuery: RoomRawQuery,
    db: NoteDatabase,
    tableName: String
) : LimitOffsetPagingSource<Note>(sourceQuery, db, tableName) {

    override suspend fun convertRows(
        limitOffsetQuery: RoomRawQuery,
        itemCount: Int
    ): List<Note> = performSuspending(db, isReadOnly = true, inTransaction = false) { connection ->
        val stmt: SQLiteStatement = connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(stmt)
        try {
            val columnIndexOfId: Int = getColumnIndexOrThrow(stmt, "id")
            val columnIndexOfTitle: Int = getColumnIndexOrThrow(stmt, "title")
            val columnIndexOfText: Int = getColumnIndexOrThrow(stmt, "text")
            val columnIndexOfDateCreated: Int = getColumnIndexOrThrow(stmt, "dateCreated")
            val columnIndexOfDateModified: Int = getColumnIndexOrThrow(stmt, "dateModified")
            val result: MutableList<Note> = mutableListOf()
            while (stmt.step()) {
                val tmpId: Long = stmt.getLong(columnIndexOfId)
                val tmpTitle: String = stmt.getText(columnIndexOfTitle)
                val tmpText: String = stmt.getText(columnIndexOfText)
                val createdTimestamp: Long? = when {
                    stmt.isNull(columnIndexOfDateCreated) -> null
                    else -> stmt.getLong(columnIndexOfDateCreated)
                }
                val tmpDateCreated: LocalDateTime =
                    when (val localDateTime = NoteTypeConverters.fromTimestamp(createdTimestamp)) {
                        null -> error("Expected NON-NULL 'kotlinx.datetime.LocalDateTime', but it was NULL.")
                        else -> localDateTime
                    }
                val modifiedTimestamp: Long? = when {
                    stmt.isNull(columnIndexOfDateModified) -> null
                    else -> stmt.getLong(columnIndexOfDateModified)
                }
                val tmpDateModified: LocalDateTime =
                    when (val localDateTime = NoteTypeConverters.fromTimestamp(modifiedTimestamp)) {
                        null -> error("Expected NON-NULL 'kotlinx.datetime.LocalDateTime', but it was NULL.")
                        else -> localDateTime
                    }
                val item = Note(tmpId, tmpTitle, tmpText, tmpDateCreated, tmpDateModified)
                result.add(item)
            }
            return@performSuspending result
        } finally {
            stmt.close()
        }
    }
}