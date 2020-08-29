package com.softartdev.notedelight.shared.database

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
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

    override val openHelper: SupportSQLiteOpenHelper = if (passphrase.isEmpty()) {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .callback(AndroidSqliteDriver.Callback(schema))
            .name(name)
            .build()
        FrameworkSQLiteOpenHelperFactory().create(configuration)
    } else SafeHelperFactory
        .fromUser(SpannableStringBuilder.valueOf(passphrase))
        .create(context, name, AndroidSqliteDriver.Callback(schema))

    private val driver = AndroidSqliteDriver(openHelper)
    private val noteDb: NoteDb = Db.getInstance(driver)

    private var openedDatabase: SupportSQLiteDatabase? = null
    private var noteDaoImpl: NoteDao? = null

    init {
        open()
    }

    @Synchronized
    private fun open(): SupportSQLiteDatabase = openedDatabase ?: run {
        val db = openHelper.writableDatabase
        openedDatabase = db
        return@run db
    }

    @Synchronized
    override fun noteDao(): NoteDao = noteDaoImpl ?: run {
        val noteQueries = noteDb.noteQueries
        val dao = NoteDaoImpl(noteQueries)
        noteDaoImpl = dao
        return@run dao
    }

    @Synchronized
    override fun close() {
        openedDatabase = null
        noteDaoImpl = null
        driver.close()
    }
}