package com.softartdev.notedelight.shared.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.softartdev.notedelight.shared.data.SafeRepo
import com.softartdev.notedelight.shared.db.Db
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.getInstance
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class NoteDatabaseImpl(
    context: Context,
    schema: SqlDriver.Schema = NoteDb.Schema,
    name: String? = SafeRepo.DB_NAME
) : NoteDatabase() {

    override val openHelper: SupportSQLiteOpenHelper = FrameworkSQLiteOpenHelperFactory()
        .create(SupportSQLiteOpenHelper.Configuration.builder(context)
        .callback(AndroidSqliteDriver.Callback(schema))
        .name(name)
        .noBackupDirectory(false)
        .build())
    private val noteDb = Db.getInstance(AndroidSqliteDriver(openHelper))
    private val noteDao: NoteDao by lazy { NoteDaoImpl(noteDb.noteQueries) }

    override fun noteDao(): NoteDao = noteDao
}