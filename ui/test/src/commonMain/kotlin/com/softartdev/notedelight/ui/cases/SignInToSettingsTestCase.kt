@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

class SignInToSettingsTestCase(
    composeUiTest: ComposeUiTest,
    private val closeSoftKeyboard: () -> Unit,
    private val pressBack: () -> Unit,
) : () -> TestResult, BaseTestCase(composeUiTest) {

    override fun invoke() = runTest {
        signInScreen {
            composeUiTest.waitUntilDisplayed("settingsButton", blockSNI = ::settingsButtonSNI)
            settingsButtonSNI.performClick()
        }
        settingsTestScreen {
            securityCategorySNI.performClick()
            composeUiTest.waitUntilDisplayed("encryptionSwitch", blockSNI = ::encryptionSwitchSNI)
            encryptionSwitchSNI.assertIsDisplayed()
                .assertIsOn()
                .performClick()

            enterPasswordDialog {
                composeUiTest.waitUntilDisplayed("enterPassword", blockSNI = ::enterPasswordSNI)
                enterPasswordSNI.performTextReplacement(DbTestEncryptor.PASSWORD)
                closeSoftKeyboard()
                confirmDialogButtonSNI.performClick()
            }
            composeUiTest.waitAssert("encryption switch is OFF", encryptionSwitchSNI::assertIsOff)
            pressBack()
            composeUiTest.awaitIdle()
            pressBack()
        }
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("emptyResultLabel", blockSNI = ::emptyResultLabelSNI)
        }
    }
}
