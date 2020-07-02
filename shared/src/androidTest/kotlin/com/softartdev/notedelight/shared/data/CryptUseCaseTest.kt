package com.softartdev.notedelight.shared.data

import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.softartdev.notedelight.shared.database.NoteDao
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@Suppress("IllegalIdentifier")
@OptIn(ExperimentalCoroutinesApi::class)
class CryptUseCaseTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val cryptUseCase = CryptUseCase(mockSafeRepo)

    @Test
    fun `check db state when db is encrypted`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(SQLCipherUtils.State.ENCRYPTED)
        assertTrue(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun `check db state when db is unencrypted`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(SQLCipherUtils.State.UNENCRYPTED)
        assertFalse(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun `check db state when db not exist`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(SQLCipherUtils.State.DOES_NOT_EXIST)
        assertFalse(cryptUseCase.dbIsEncrypted())
    }

    @Test
    fun `check correct password`() = mainCoroutineRule.runBlockingTest {
        val mockNoteDao = Mockito.mock(NoteDao::class.java)
        Mockito.`when`(mockSafeRepo.noteDao).thenReturn(mockNoteDao)
        Mockito.`when`(mockNoteDao.getNotes()).thenReturn(flowOf(emptyList()))
        val pass = StubEditable("correct password")
        assertTrue(cryptUseCase.checkPassword(pass))
    }

    @Test
    fun `check incorrect password`() = mainCoroutineRule.runBlockingTest {
        val pass = StubEditable("incorrect password")
        assertFalse(cryptUseCase.checkPassword(pass))
    }

    @Test
    fun `change password for decrypt`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(SQLCipherUtils.State.ENCRYPTED)
        val oldPass = StubEditable("old password")
        val newPass = null
        cryptUseCase.changePassword(oldPass, newPass)
        Mockito.verify(mockSafeRepo).decrypt(oldPass)
    }

    @Test
    fun `change password for rekey`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(SQLCipherUtils.State.ENCRYPTED)
        val oldPass = StubEditable("old password")
        val newPass = StubEditable("new password")
        cryptUseCase.changePassword(oldPass, newPass)
        Mockito.verify(mockSafeRepo).rekey(oldPass, newPass)
    }

    @Test
    fun `change password for encrypt`() {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(SQLCipherUtils.State.UNENCRYPTED)
        val oldPass = null
        val newPass = StubEditable("new password")
        cryptUseCase.changePassword(oldPass, newPass)
        Mockito.verify(mockSafeRepo).encrypt(newPass)
    }

}