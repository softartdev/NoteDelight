package com.softartdev.notedelight.presentation.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.MainDispatcherRule
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SplashViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)

    @Test
    fun navSignIn() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.ENCRYPTED)
        val splashViewModel = SplashViewModel(mockSafeRepo, mockRouter)
        splashViewModel.stateFlow.test {
            assertEquals(true, awaitItem())

            splashViewModel.checkEncryption()

            Mockito.verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.SignIn)
            assertEquals(false, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenReturn(PlatformSQLiteState.UNENCRYPTED)
        val splashViewModel = SplashViewModel(mockSafeRepo, mockRouter)
        splashViewModel.stateFlow.test {
            assertEquals(true, awaitItem())

            splashViewModel.checkEncryption()

            Mockito.verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.Main)
            assertEquals(false, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun showError() = runTest {
        Mockito.`when`(mockSafeRepo.databaseState).thenThrow(RuntimeException::class.java)
        val splashViewModel = SplashViewModel(mockSafeRepo, mockRouter)
        splashViewModel.stateFlow.test {
            assertEquals(true, awaitItem())

            splashViewModel.checkEncryption()

            Mockito.verify(mockRouter).navigate(
                route = AppNavGraph.ErrorDialog(message = null)
            )
            assertEquals(false, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}