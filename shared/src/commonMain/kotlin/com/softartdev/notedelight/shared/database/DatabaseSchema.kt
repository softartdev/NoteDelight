package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.date.DateAdapter
import com.softartdev.notedelight.shared.date.getSystemTimeInMillis
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun createQueryWrapper(sqlDriver: SqlDriver): NoteDb {
    val dateColumnAdapter = DateAdapter()
    val noteColumnAdapter = Note.Adapter(dateColumnAdapter, dateColumnAdapter)
    return NoteDb(
        driver = sqlDriver,
        noteAdapter = noteColumnAdapter
    )
}

object TestSchema {
    private val ldt: LocalDateTime//FIXME cannot use createLocalDateTime() because nanoseconds don't save to db therefore tests fail
        get() = Instant.fromEpochMilliseconds(
            epochMilliseconds = getSystemTimeInMillis()
        ).toLocalDateTime(TimeZone.currentSystemDefault())

    val firstNote = Note(1, "first title from test schema", "first text", ldt, ldt)
    val secondNote = Note(2, "second title", "second text", ldt, ldt)
    val thirdNote = Note(3, "third title", "third text", ldt, ldt)

    fun insertTestNotes(noteQueries: NoteQueries) =
        sequenceOf(firstNote, secondNote, thirdNote)
            .forEach(noteQueries::insert)
}
