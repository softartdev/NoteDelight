package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.PlatformSQLiteState
import com.softartdev.notedelight.shared.database.SafeRepo
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.createQueryWrapper
import com.softartdev.notedelight.shared.test.util.StubEditable
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

@Suppress("IllegalIdentifier")
@OptIn(ExperimentalCoroutinesApi::class)
class CryptUseCaseTest {

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val cryptUseCase = CryptUseCase(mockSafeRepo)

    @Test
    fun `check db state when db is encrypted`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        assertTrue(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun `check db state when db is unencrypted`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.UNENCRYPTED)
        assertFalse(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun `check db state when db not exist`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.DOES_NOT_EXIST)
        assertFalse(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun `check correct password`() = runBlocking {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        NoteDb.Schema.create(driver)
        val noteDb = createQueryWrapper(driver)
        Mockito.`when`(mockSafeRepo.noteQueries).thenReturn(noteDb.noteQueries)
        val pass = StubEditable("correct password")
        assertTrue(cryptUseCase.checkPassword(pass))
    }

    @Test
    fun `check incorrect password`() = runBlocking {
        val pass = StubEditable("incorrect password")
        assertFalse(cryptUseCase.checkPassword(pass))
    }

    @Test
    fun `change password for decrypt`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        val oldPass = StubEditable("old password")
        val newPass = null
        cryptUseCase.changePassword(oldPass, newPass)
        Mockito.verify(mockSafeRepo).decrypt(oldPass)
    }

    @Test
    fun `change password for rekey`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        val oldPass = StubEditable("old password")
        val newPass = StubEditable("new password")
        cryptUseCase.changePassword(oldPass, newPass)
        Mockito.verify(mockSafeRepo).rekey(oldPass, newPass)
    }

    @Test
    fun `change password for encrypt`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.UNENCRYPTED)
        val oldPass = null
        val newPass = StubEditable("new password")
        cryptUseCase.changePassword(oldPass, newPass)
        Mockito.verify(mockSafeRepo).encrypt(newPass)
    }

}