package com.softartdev.notedelight.db

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.driver.SupportSQLiteDriver
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.Dispatchers

class AndroidDatabaseHolder(
    context: Context,
    passphrase: CharSequence,
) : RoomDbHolder {

    val openHelper: SupportSQLiteOpenHelper = SafeHelperFactory
        .fromUser(
            SpannableStringBuilder(passphrase),
            SafeHelperFactory.Options.builder()
                // Room 3 may ask the driver to open the helper more than once while it
                // configures the database. Keep the key until this holder is closed.
                .setClearPassphrase(false)
                .build(),
        )
        .create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(SafeRepo.DB_NAME)
                .callback(Room3SupportCallback())
                .build(),
        )

    val noteDatabase: NoteDatabase = Room
        .databaseBuilder<NoteDatabase>(context, SafeRepo.DB_NAME)
        .setDriver(SupportSQLiteDriver(openHelper))
        .addMigrations(NOTE_DATABASE_MIGRATION_1_2)
        // Room 3 enables WAL by issuing PRAGMAs through SQLiteDriver, but SafeRoom's
        // SupportSQLiteOpenHelper is not told that WAL is enabled. On a cold Android
        // process start the helper can therefore discard the previous process' WAL
        // before Room gets a connection, losing recently committed notes. Keep the
        // journal in the main database file for this SQLCipher-backed driver.
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigrationOnDowngrade(false)
        .build()

    override fun close() {
        noteDatabase.close()
        openHelper.close()
    }
}
