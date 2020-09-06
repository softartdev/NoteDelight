package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.date.DateAdapter
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries
import com.squareup.sqldelight.db.SqlDriver

fun createQueryWrapper(sqlDriver: SqlDriver): NoteDb {
    val dateColumnAdapter = DateAdapter()
    val noteColumnAdapter = Note.Adapter(dateColumnAdapter, dateColumnAdapter)
    return NoteDb(
        driver = sqlDriver,
        noteAdapter = noteColumnAdapter
    )
}

object TestSchema {
    private val ldt get() = createLocalDateTime()

    val firstNote = Note(1, "first title from test schema", "first text", ldt, ldt)
    val secondNote = Note(2, "second title", "second text", ldt, ldt)
    val thirdNote = Note(3, "third title", "third text", ldt, ldt)

    fun insertTestNotes(noteQueries: NoteQueries) =
        sequenceOf(firstNote, secondNote, thirdNote)
            .forEach(noteQueries::insert)
}
