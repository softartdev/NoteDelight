package com.softartdev.notedelight.db

import androidx.room3.Room
import com.softartdev.notedelight.repository.SafeRepo

class IosDatabaseHolder(
    key: String? = null,
    name: String = SafeRepo.DB_NAME,
) : RoomDbHolder {

    val noteDatabase: NoteDatabase = Room
        .databaseBuilder<NoteDatabase>(name = IosCipherUtils.getDatabasePath(name))
        .setDriver(IosCipherDriver(key))
        .addMigrations(NOTE_DATABASE_MIGRATION_1_2)
        .fallbackToDestructiveMigrationOnDowngrade(false)
        .build()

    override fun close() = noteDatabase.close()
}
