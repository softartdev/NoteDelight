package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteDb

class NoteDatabaseImpl(
    private val noteDb: NoteDb
) : NoteDatabase() {

    private val noteDao: NoteDao by lazy { NoteDaoImpl(noteDb.noteQueries) }

    override fun noteDao(): NoteDao = noteDao
}