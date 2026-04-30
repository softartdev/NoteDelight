@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.interactor.BiometricInteractor
import com.softartdev.notedelight.interactor.TestBiometricInteractor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.util.BIOMETRIC_DISABLE_CONFIRMATION_DIALOG_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_TAG
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitUntilNotExist
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.koin.mp.KoinPlatformTools
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class BiometricSettingsTestCase(
    composeUiTest: ComposeUiTest,
    private val closeSoftKeyboard: () -> Unit,
) : () -> TestResult, BaseTestCase(composeUiTest) {

    override fun invoke() = runTest(timeout = 3.minutes) {
        val biometricInteractor: TestBiometricInteractor =
            KoinPlatformTools.defaultContext().get().get(TestBiometricInteractor::class)
        biometricInteractor.reset(canAuthenticateResult = true)
        val password = "biometric-password"

        mainTestScreen {
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
        }
        settingsTestScreen {
            composeUiTest.waitUntilDisplayed("securityCategory", blockSNI = ::securityCategorySNI)
            securityCategorySNI.performClick()
            composeUiTest.waitUntilDisplayed("encryptionSwitch", blockSNI = ::encryptionSwitchSNI)
            encryptionSwitchSNI.assertIsOff().performClick()
        }
        confirmPasswordDialog {
            composeUiTest.waitUntilDisplayed("confirmPasswordDialog", blockSNI = ::dialogSNI)
            confirmPasswordSNI.performTextReplacement(password)
            closeSoftKeyboard()
            confirmRepeatPasswordSNI.performTextReplacement(password)
            closeSoftKeyboard()
            confirmDialogButtonSNI.performSemanticsAction(SemanticsActions.OnClick)
        }
        composeUiTest.waitUntilNotExist(CONFIRM_PASSWORD_DIALOG_TAG)
        settingsTestScreen {
            composeUiTest.waitAssert("encryption switch is ON", encryptionSwitchSNI::assertIsOn)
            composeUiTest.waitUntilDisplayed("biometricSwitch", blockSNI = ::biometricSwitchSNI)
            biometricSwitchSNI.assertIsOff().performClick()
        }
        biometricEnrollDialog {
            composeUiTest.waitUntilDisplayed("biometricEnrollDialog", blockSNI = ::dialogSNI)
            passwordSNI.performTextReplacement(password)
            closeSoftKeyboard()
            confirmDialogButtonSNI.performSemanticsAction(SemanticsActions.OnClick)
        }
        composeUiTest.waitUntilNotExist(BIOMETRIC_ENROLL_DIALOG_TAG)
        settingsTestScreen {
            composeUiTest.waitAssert("biometric switch is ON", biometricSwitchSNI::assertIsOn)
            biometricSwitchSNI.performClick()
        }
        biometricDisableConfirmationDialog {
            composeUiTest.waitUntilDisplayed("biometricDisableConfirmationDialog", blockSNI = ::dialogSNI)
            confirmDialogButtonSNI.performSemanticsAction(SemanticsActions.OnClick)
        }
        composeUiTest.waitUntilNotExist(BIOMETRIC_DISABLE_CONFIRMATION_DIALOG_TAG)
        settingsTestScreen {
            composeUiTest.waitAssert("biometric switch is OFF", biometricSwitchSNI::assertIsOff)
        }
        assertNull(biometricInteractor.storedPassword)
        assertEquals(1, biometricInteractor.clearStoredPasswordCount)
        BiometricInteractor.disableDialogChannel.tryReceive()
    }
}
