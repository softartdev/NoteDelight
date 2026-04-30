@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.performClick
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.interactor.TestBiometricInteractor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.koin.mp.KoinPlatformTools

class BiometricSignInTestCase(
    composeUiTest: ComposeUiTest,
) : () -> TestResult, BaseTestCase(composeUiTest) {

    override fun invoke() = runTest {
        val biometricInteractor: TestBiometricInteractor =
            KoinPlatformTools.defaultContext().get().get(TestBiometricInteractor::class)
        biometricInteractor.reset(
            canAuthenticateResult = true,
            storedPassword = DbTestEncryptor.PASSWORD,
        )
        signInScreen {
            composeUiTest.waitUntilDisplayed("biometricButton", blockSNI = ::biometricButtonSNI)
            biometricButtonSNI.performClick()
        }
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("emptyResultLabel", blockSNI = ::emptyResultLabelSNI)
        }
    }
}
