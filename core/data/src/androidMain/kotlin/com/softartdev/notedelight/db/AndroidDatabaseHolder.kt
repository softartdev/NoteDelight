package com.softartdev.notedelight.db

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.repository.SafeRepo

class AndroidDatabaseHolder(
    context: Context,
    passphrase: CharSequence,
    schema: SqlSchema<QueryResult.Value<Unit>> = NoteDb.Schema,
    name: String? = SafeRepo.DB_NAME
) : SqlDelightDbHolder {

    private val openHelper: SupportSQLiteOpenHelper = SafeHelperFactory
        .fromUser(SpannableStringBuilder.valueOf(passphrase))
        .create(context, name, AndroidSqliteDriver.Callback(schema))

    val openDatabase: SupportSQLiteDatabase = openHelper.writableDatabase

    override val driver = AndroidSqliteDriver(openDatabase)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}