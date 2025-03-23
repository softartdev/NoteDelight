package com.softartdev.notedelight.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.util.Properties

class JdbcDatabaseHolder(props: Properties = Properties()) : RoomDbHolder {
    val noteDatabase: NoteDatabase = Room
        .databaseBuilder<NoteDatabase>(name = FilePathResolver().invoke())
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

    override fun close() = noteDatabase.close()
}