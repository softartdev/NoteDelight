package com.softartdev.notedelight.shared.db

object NoteData {
    fun notes() = Db.instance.noteQueries.getAll().executeAsList()
}