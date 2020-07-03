package com.softartdev.notedelight.shared.database

class NoteDatabaseImpl : NoteDatabase() {

    private val noteDao = NoteDaoImpl()

    override fun noteDao(): NoteDao = noteDao
}