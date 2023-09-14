package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed

class FlowAfterCryptTestCase(
    composeTestRule: ComposeContentTestRule,
    private val pressBack: () -> Unit,
    private val closeSoftKeyboard: () -> Unit,
    private val password: String = "password"
) : () -> Unit, BaseTestCase(composeTestRule) {

    private val titleText = "Lorem"

    override fun invoke() {
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                encryptionSwitchSNI.assertIsOff()
                    .performClick()
                confirmPasswordDialog {
                    composeTestRule.waitUntilDisplayed(blockSNI = ::confirmPasswordSNI)
                    confirmPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    confirmRepeatPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOn)
                pressBack()
            }
            fabSNI.performClick()
            noteScreen {
                textFieldSNI.performTextInput(titleText)
                closeSoftKeyboard()
                pressBack()
                composeTestRule.waitForIdle()
            }
            commonDialog {
                yesDialogButtonSNI.performClick()
            }
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                encryptionSwitchSNI.assertIsOn()
                    .performClick()
                enterPasswordDialog {
                    enterPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOff)
                pressBack()
            }
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            noteScreen {
                deleteNoteMenuButtonSNI.performClick()
            }
            commonDialog {
                composeTestRule.waitUntilDisplayed(blockSNI = ::yesDialogButtonSNI)
                yesDialogButtonSNI.performClick()
            }
            composeTestRule.waitUntilDisplayed(blockSNI = ::labelEmptyResultSNI)
            settingsMenuButtonSNI.performClick()
        }
    }
}