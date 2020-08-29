package com.softartdev.notedelight.shared.database

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.data.SafeRepo
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.createQueryWrapper
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class NoteDatabaseImpl(
    context: Context,
    passphrase: CharSequence,
    schema: SqlDriver.Schema = NoteDb.Schema,
    name: String? = SafeRepo.DB_NAME
) : NoteDatabase() {

    override val openHelper: SupportSQLiteOpenHelper = if (passphrase.isEmpty()) {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .callback(AndroidSqliteDriver.Callback(schema))
            .name(name)
            .build()
        FrameworkSQLiteOpenHelperFactory().create(configuration)
    } else SafeHelperFactory
        .fromUser(SpannableStringBuilder.valueOf(passphrase))
        .create(context, name, AndroidSqliteDriver.Callback(schema))
    private val openDatabase: SupportSQLiteDatabase = openHelper.writableDatabase
    private val driver = AndroidSqliteDriver(openDatabase)
    private val noteDb: NoteDb = createQueryWrapper(driver)
    private val noteQueries = noteDb.noteQueries
    private val noteDaoImpl = NoteDaoImpl(noteQueries)

    override fun noteDao(): NoteDao = noteDaoImpl

    override fun close() = driver.close()
}