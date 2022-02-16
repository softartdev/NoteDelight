package com.softartdev.notedelight.old

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.shared.database.DatabaseRepo.Companion.DB_NAME
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.database.createQueryWrapper
import com.softartdev.notedelight.shared.db.NoteDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class SqlDelightCipherInstrumentedTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val databaseState: SQLCipherUtils.State
        get() = SQLCipherUtils.getDatabaseState(context, DB_NAME)

    @Test
    fun primitiveTest() {
        assertEquals(SQLCipherUtils.State.DOES_NOT_EXIST, databaseState)

        val emptyPassword: Editable = SpannableStringBuilder.valueOf("")
        val callback: SupportSQLiteOpenHelper.Callback = AndroidSqliteDriver.Callback(NoteDb.Schema)
        var openDatabase: SupportSQLiteDatabase = SafeHelperFactory
            .fromUser(emptyPassword)
            .create(context, DB_NAME, callback)
            .writableDatabase

        assertEquals(SQLCipherUtils.State.UNENCRYPTED, databaseState)

        var driver: SqlDriver = AndroidSqliteDriver(openDatabase)
        var noteQueries = createQueryWrapper(driver).noteQueries

        //ZERO STEP
        TestSchema.insertTestNotes(noteQueries)
        var exp = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
        assertEquals(exp, noteQueries.getAll().executeAsList())

        SQLCipherUtils.encrypt(context, DB_NAME, "password".toCharArray())// db close inside
        assertEquals(SQLCipherUtils.State.ENCRYPTED, databaseState)

        val password = SpannableStringBuilder.valueOf("password")
        openDatabase = SafeHelperFactory
            .fromUser(password)
            .create(context, DB_NAME, callback)
            .writableDatabase
        driver = AndroidSqliteDriver(openDatabase)
        noteQueries = createQueryWrapper(driver).noteQueries

        //FIRST STEP
        noteQueries.delete(TestSchema.firstNote.id)
        exp = listOf(TestSchema.secondNote, TestSchema.thirdNote)
        assertEquals(exp, noteQueries.getAll().executeAsList())

        SafeHelperFactory.rekey(openDatabase, "new password".toCharArray())

        //SECOND STEP
        noteQueries.delete(TestSchema.secondNote.id)
        exp = listOf(TestSchema.thirdNote)
        assertEquals(exp, noteQueries.getAll().executeAsList())

        val originalFile = context.getDatabasePath(DB_NAME)
        SQLCipherUtils.decrypt(context, originalFile, "new password".toCharArray())
        assertEquals(SQLCipherUtils.State.UNENCRYPTED, databaseState)

        openDatabase = SafeHelperFactory
            .fromUser(emptyPassword)
            .create(context, DB_NAME, callback)
            .writableDatabase
        driver = AndroidSqliteDriver(openDatabase)
        noteQueries = createQueryWrapper(driver).noteQueries

        //THIRD STEP
        noteQueries.delete(TestSchema.thirdNote.id)
        exp = listOf()
        assertEquals(exp, noteQueries.getAll().executeAsList())
    }
}