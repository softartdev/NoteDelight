package com.softartdev.notedelight.db

import androidx.room3.Room
import kotlinx.coroutines.Dispatchers
import java.util.Properties

class JdbcDatabaseHolder(
    props: Properties = Properties(),
    dbPath: String = FilePathResolver().invoke(),
) : RoomDbHolder {
    val noteDatabase: NoteDatabase = Room
        .databaseBuilder<NoteDatabase>(name = dbPath)
        .setDriver(JdbcSQLiteDriver(props))
        .addMigrations(NOTE_DATABASE_MIGRATION_1_2)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

    override fun close() = noteDatabase.close()
}
