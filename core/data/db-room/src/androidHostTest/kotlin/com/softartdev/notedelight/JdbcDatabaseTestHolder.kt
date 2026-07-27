package com.softartdev.notedelight

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.softartdev.notedelight.db.NoteDatabase
import com.softartdev.notedelight.db.RoomDbHolder
import kotlinx.coroutines.Dispatchers

class JdbcDatabaseTestHolder : RoomDbHolder {
    val noteDatabase: NoteDatabase = Room
        .inMemoryDatabaseBuilder<NoteDatabase>()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

    override fun close() = noteDatabase.close()
}
