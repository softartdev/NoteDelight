@file:OptIn(ExperimentalForeignApi::class)

package com.softartdev.notedelight.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.cinterop.ExperimentalForeignApi

class IosDatabaseHolder(
    key: String? = null,
    rekey: String? = null,
    name: String = SafeRepo.DB_NAME,
) : RoomDbHolder {

    val noteDatabase: NoteDatabase = Room
        .databaseBuilder<NoteDatabase>(name = IosCipherUtils.getDatabasePath(name))
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigrationOnDowngrade(false)
        .build()

    override fun close() = noteDatabase.close()
}