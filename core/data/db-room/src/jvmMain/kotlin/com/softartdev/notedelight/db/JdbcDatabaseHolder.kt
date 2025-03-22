package com.softartdev.notedelight.db

import androidx.room.Room
import java.util.Properties

class JdbcDatabaseHolder(props: Properties = Properties()) : RoomDbHolder {
    val noteDatabase: NoteDatabase = Room
        .databaseBuilder<NoteDatabase>(name = FilePathResolver().invoke())
        .build()

    override fun close() = noteDatabase.close()
}