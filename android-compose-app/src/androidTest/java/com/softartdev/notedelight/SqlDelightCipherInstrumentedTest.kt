package com.softartdev.notedelight

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.db.TestSchema.firstNote
import com.softartdev.notedelight.db.TestSchema.secondNote
import com.softartdev.notedelight.db.TestSchema.thirdNote
import com.softartdev.notedelight.db.createQueryWrapper
import com.softartdev.notedelight.repository.SafeRepo.Companion.DB_NAME
import com.softartdev.notedelight.shared.db.Note
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
        var exp = listOf(firstNote, secondNote, thirdNote).sortedBy(Note::id)
        val act = noteQueries.getAll().executeAsList().sortedBy(Note::id)
        assertEquals(exp, act)

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
        noteQueries.delete(firstNote.id)
        exp = listOf(secondNote, thirdNote)
        assertEquals(exp, noteQueries.getAll().executeAsList())

        SafeHelperFactory.rekey(openDatabase, "new password".toCharArray())

        //SECOND STEP
        noteQueries.delete(secondNote.id)
        exp = listOf(thirdNote)
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
        noteQueries.delete(thirdNote.id)
        exp = listOf()
        assertEquals(exp, noteQueries.getAll().executeAsList())
    }
}