package com.softartdev.notedelight.shared.database

import androidx.sqlite.db.SupportSQLiteOpenHelper


abstract class NoteDatabase {

    abstract val openHelper: SupportSQLiteOpenHelper

    abstract fun noteDao(): NoteDao
}
