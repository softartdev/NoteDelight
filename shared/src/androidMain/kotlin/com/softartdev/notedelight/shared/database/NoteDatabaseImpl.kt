package com.softartdev.notedelight.shared.database

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.data.SafeRepo
import com.softartdev.notedelight.shared.db.Db
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.getInstance
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class NoteDatabaseImpl(
    context: Context,
    passphrase: CharSequence,
    schema: SqlDriver.Schema = NoteDb.Schema,
    name: String? = SafeRepo.DB_NAME
) : NoteDatabase() {

    override val openHelper: SupportSQLiteOpenHelper = SafeHelperFactory
        .fromUser(SpannableStringBuilder.valueOf(passphrase))
        .create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .callback(AndroidSqliteDriver.Callback(schema))
                .name(name)
                .build()
        )
    private val noteDb = Db.getInstance(AndroidSqliteDriver(openHelper))

    override fun noteDao(): NoteDao = NoteDaoImpl(noteDb.noteQueries)
}