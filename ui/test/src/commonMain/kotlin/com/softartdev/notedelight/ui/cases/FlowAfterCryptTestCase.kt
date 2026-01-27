@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.retryUntilDisplayed
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitUntilNotExist
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

class FlowAfterCryptTestCase(
    composeUiTest: ComposeUiTest,
    private val pressBack: () -> Unit,
    private val closeSoftKeyboard: () -> Unit,
    private val password: String = "password"
) : () -> TestResult, BaseTestCase(composeUiTest) {
    private val logger = Logger.withTag("FlowAfterCryptTestCase")
    private val titleText = "Lorem"

    override fun invoke() = runTest {
        logger.i { "Starting FlowAfterCryptTestCase" }
        MainTestScreen.noteItemTitleText = titleText
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
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
                    confirmDialogButtonSNI.performClick()
                    composeUiTest.awaitIdle()
                    composeUiTest.waitUntilNotExist(tag = CONFIRM_PASSWORD_DIALOG_TAG)
                }
                composeUiTest.waitAssert("encrypt switch is ON", encryptionSwitchSNI::assertIsOn)
                pressBack()
            }
            fabSNI.performClick()
            noteScreen {
                composeUiTest.waitUntilDisplayed("noteTextField", blockSNI = ::textFieldSNI)
                textFieldSNI.performTextInput(titleText)
                closeSoftKeyboard()
                pressBack()
                composeUiTest.waitForIdle()
            }
            commonDialog {
                confirmDialogButtonSNI.performClick()
            }
            composeUiTest.waitUntilDisplayed("noteListItem#1", blockSNI = ::noteListItemSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                encryptionSwitchSNI.assertIsOn()
                    .performClick()
                enterPasswordDialog {
                    enterPasswordSNI.performTextReplacement(text = password)
                    closeSoftKeyboard()
                    confirmDialogButtonSNI.performClick()
                }
                composeUiTest.waitAssert("encrypt switch is OFF", encryptionSwitchSNI::assertIsOff)
                pressBack()
            }
            composeUiTest.waitUntilDisplayed("noteListItem#2", blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            noteScreen {
                deleteNoteMenuButtonSNI.performClick()
            }
            commonDialog {
                composeUiTest.waitUntilDisplayed("yesDialogButton", blockSNI = ::confirmDialogButtonSNI)
                confirmDialogButtonSNI.performClick()
            }
            composeUiTest.waitUntilDisplayed("labelEmptyResult", blockSNI = ::labelEmptyResultSNI)
            settingsMenuButtonSNI.performClick()
        }
    }
}