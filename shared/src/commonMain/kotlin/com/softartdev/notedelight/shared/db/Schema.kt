package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.db.SqlDriver

fun createQueryWrapper(sqlDriver: SqlDriver): NoteDb {
    val dateColumnAdapter = DateAdapter()
    val noteColumnAdapter = Note.Adapter(dateColumnAdapter, dateColumnAdapter)
    return NoteDb(
        driver = sqlDriver,
        noteAdapter = noteColumnAdapter
    )
}

object TestSchema : SqlDriver.Schema by NoteDb.Schema {
    private val firstNote = Note(1, "first title", "first text", Date(), Date())
    private val secondNote = Note(2, "second title", "second text", Date(), Date())
    private val thirdNote = Note(3, "third title", "third text", Date(), Date())

    override fun create(driver: SqlDriver) {
        NoteDb.Schema.create(driver)

        // Seed data time!
        createQueryWrapper(driver).apply {
            sequenceOf(
                firstNote,
                secondNote,
                thirdNote
            ).forEach(noteQueries::insert)
        }
    }
}
