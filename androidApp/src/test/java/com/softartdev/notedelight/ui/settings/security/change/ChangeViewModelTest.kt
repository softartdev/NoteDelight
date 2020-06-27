package com.softartdev.notedelight.ui.settings.security.change

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.StubEditable
import com.softartdev.notedelight.shared.test.util.assertValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class ChangeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val cryptUseCase = Mockito.mock(CryptUseCase::class.java)
    private val changeViewModel = ChangeViewModel(cryptUseCase)

    @Test
    fun checkChangeOldEmptyPasswordError() = changeViewModel.resultLiveData.assertValues(
            ChangeResult.Loading,
            ChangeResult.OldEmptyPasswordError
    ) {
        val old = StubEditable("")
        val new = StubEditable("new")
        changeViewModel.checkChange(old, new, new)
    }

    @Test
    fun checkChangeNewEmptyPasswordError() = changeViewModel.resultLiveData.assertValues(
            ChangeResult.Loading,
            ChangeResult.NewEmptyPasswordError
    ) {
        val old = StubEditable("old")
        val new = StubEditable("")
        changeViewModel.checkChange(old, new, new)
    }

    @Test
    fun checkChangePasswordsNoMatchError() = changeViewModel.resultLiveData.assertValues(
            ChangeResult.Loading,
            ChangeResult.PasswordsNoMatchError
    ) {
        val old = StubEditable("old")
        val new = StubEditable("new")
        val rep = StubEditable("rep")
        changeViewModel.checkChange(old, new, rep)
    }

    @Test
    fun checkChangeSuccess() = mainCoroutineRule.runBlockingTest {
        val old = StubEditable("old")
        Mockito.`when`(cryptUseCase.checkPassword(old)).thenReturn(true)
        changeViewModel.resultLiveData.assertValues(
                ChangeResult.Loading,
                ChangeResult.Success
        ) {
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
        }
    }

    @Test
    fun checkChangeIncorrectPasswordError() = mainCoroutineRule.runBlockingTest {
        val old = StubEditable("old")
        Mockito.`when`(cryptUseCase.checkPassword(old)).thenReturn(false)
        changeViewModel.resultLiveData.assertValues(
                ChangeResult.Loading,
                ChangeResult.IncorrectPasswordError
        ) {
            val new = StubEditable("new")
            changeViewModel.checkChange(old, new, new)
        }
    }

    @Test
    fun errorResult() {
        assertEquals(ChangeResult.Error("err"), changeViewModel.errorResult(Throwable("err")))
    }
}