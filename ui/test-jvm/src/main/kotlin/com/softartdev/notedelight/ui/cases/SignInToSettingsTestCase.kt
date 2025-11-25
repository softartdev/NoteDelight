package com.softartdev.notedelight.ui.cases

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.runTest

class SignInToSettingsTestCase(
    composeTestRule: ComposeContentTestRule,
    private val closeSoftKeyboard: () -> Unit,
) : () -> Unit, BaseTestCase(composeTestRule) {

    override fun invoke() = runTest {
        signInScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::settingsButtonSNI)
            settingsButtonSNI.performClick()
        }
        settingsTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::encryptionSwitchSNI)
            encryptionSwitchSNI.assertIsDisplayed()
                .assertIsOn()
                .performClick()

            enterPasswordDialog {
                composeTestRule.waitUntilDisplayed(blockSNI = ::enterPasswordSNI)
                enterPasswordSNI.performTextReplacement(DbTestEncryptor.PASSWORD)
                closeSoftKeyboard()
                yesDialogButtonSNI.performClick()
            }
            composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOff)

            composeTestRule.onNodeWithContentDescription(Icons.AutoMirrored.Filled.ArrowBack.name)
                .performClick()
        }
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::emptyResultLabelSNI)
        }
    }
}
