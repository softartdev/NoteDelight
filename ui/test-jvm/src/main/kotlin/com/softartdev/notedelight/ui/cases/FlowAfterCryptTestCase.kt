package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.retryUntilDisplayed
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.runTest

class FlowAfterCryptTestCase(
    composeTestRule: ComposeContentTestRule,
    private val pressBack: () -> Unit,
    private val closeSoftKeyboard: () -> Unit,
    private val password: String = "password"
) : () -> Unit, BaseTestCase(composeTestRule) {
    private val logger = Logger.withTag("FlowAfterCryptTestCase")
    private val titleText = "Lorem"

    override fun invoke() = runTest {
        logger.i { "Starting FlowAfterCryptTestCase" }
        MainTestScreen.noteItemTitleText = titleText
        mainTestScreen {
            composeTestRule.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                encryptionSwitchSNI.assertIsOff()
                    .performClick()
                confirmPasswordDialog {
                    retryUntilDisplayed(
                        description = "Confirm Password Field",
                        action = encryptionSwitchSNI::performClick,
                        sni = confirmPasswordSNI
                    )
                    confirmPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    confirmRepeatPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert("encrypt switch is ON", encryptionSwitchSNI::assertIsOn)
                pressBack()
            }
            fabSNI.performClick()
            noteScreen {
                composeTestRule.waitUntilDisplayed("noteTextField", blockSNI = ::textFieldSNI)
                textFieldSNI.performTextInput(titleText)
                closeSoftKeyboard()
                pressBack()
                composeTestRule.waitForIdle()
            }
            commonDialog {
                yesDialogButtonSNI.performClick()
            }
            composeTestRule.waitUntilDisplayed("noteListItem#1", blockSNI = ::noteListItemSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                encryptionSwitchSNI.assertIsOn()
                    .performClick()
                enterPasswordDialog {
                    enterPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert("encrypt switch is OFF", encryptionSwitchSNI::assertIsOff)
                pressBack()
            }
            composeTestRule.waitUntilDisplayed("noteListItem#2", blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            noteScreen {
                deleteNoteMenuButtonSNI.performClick()
            }
            commonDialog {
                composeTestRule.waitUntilDisplayed("yesDialogButton", blockSNI = ::yesDialogButtonSNI)
                yesDialogButtonSNI.performClick()
            }
            composeTestRule.waitUntilDisplayed("labelEmptyResult", blockSNI = ::labelEmptyResultSNI)
            settingsMenuButtonSNI.performClick()
        }
    }
}