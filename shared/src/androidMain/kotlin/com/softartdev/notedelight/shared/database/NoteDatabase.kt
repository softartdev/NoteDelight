package com.softartdev.notedelight.shared.database


abstract class NoteDatabase {
    abstract fun noteDao(): NoteDao
}
