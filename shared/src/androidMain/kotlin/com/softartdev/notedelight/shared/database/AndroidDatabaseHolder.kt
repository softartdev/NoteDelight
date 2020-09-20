package com.softartdev.notedelight.shared.database

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.db.NoteDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class AndroidDatabaseHolder(
    context: Context,
    passphrase: CharSequence,
    schema: SqlDriver.Schema = NoteDb.Schema,
    name: String? = DatabaseRepo.DB_NAME
) : DatabaseHolder() {
    private val openHelper: SupportSQLiteOpenHelper = SafeHelperFactory
        .fromUser(SpannableStringBuilder.valueOf(passphrase))
        .create(context, name, AndroidSqliteDriver.Callback(schema))
    val openDatabase: SupportSQLiteDatabase = openHelper.writableDatabase
    override val driver = AndroidSqliteDriver(openDatabase)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}