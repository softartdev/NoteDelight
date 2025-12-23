@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.retryUntilDisplayed
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.dialog.security.CHANGE_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.ui.dialog.security.CONFIRM_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.ui.dialog.security.ENTER_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitUntilNotExist
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.empty_password
import notedelight.ui.shared.generated.resources.incorrect_password
import notedelight.ui.shared.generated.resources.passwords_do_not_match
import org.jetbrains.compose.resources.getString

class SettingPasswordTestCase(
    composeUiTest: ComposeUiTest,
    private val closeSoftKeyboard: () -> Unit,
) : () -> TestResult, BaseTestCase(composeUiTest) {
    private val logger = Logger.withTag("ℹ️SettingPasswordTestCase")

    override fun invoke() = runTest {
        logger.i { "Starting SettingPasswordTestCase" }
        val emptyPassTitle = getString(Res.string.empty_password)
        val passDoNotMatchTitle = getString(Res.string.passwords_do_not_match)
        val incorrectPassTitle = getString(Res.string.incorrect_password)
        mainTestScreen {
            logger.i { "MainTestScreen: Opening settings screen" }
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
        }
        settingsTestScreen {
            logger.i { "SettingsTestScreen: Toggling encryption switch ON" }
            encryptionSwitchSNI
                .assertIsOff()
                .performClick()

            confirmPasswordDialog {
                logger.i { "ConfirmPasswordDialog: Setting password with various inputs" }
                retryUntilDisplayed(
                    description = "ConfirmPasswordDialog",
                    action = encryptionSwitchSNI::performClick,
                    sni = dialogSNI
                )
                confirmPasswordSNI.performClick()
                confirmLabelSNI.assertIsDisplayed()
                confirmRepeatPasswordSNI.assertIsDisplayed()
                confirmRepeatLabelSNI.assertIsDisplayed()

                confirmVisibilitySNI.assertIsDisplayed()
                    .performClick()
                confirmRepeatVisibilitySNI.assertIsDisplayed()
                    .performClick()

                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("confirm label has empty pass text") {
                    confirmLabelSNI.assertTextEquals(emptyPassTitle)
                }
                confirmPasswordSNI.performTextReplacement(text = "1")
                closeSoftKeyboard()

                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("confirm repeat label has empty pass text") {
                    confirmRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)
                }
                confirmRepeatPasswordSNI.performTextReplacement(text = "2")

                confirmDialogButtonSNI.performClick()
                confirmRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)

                confirmRepeatPasswordSNI.performTextReplacement(text = "1")
                confirmDialogButtonSNI.performClick()
                composeUiTest.awaitIdle()
                composeUiTest.waitUntilNotExist(tag = CONFIRM_PASSWORD_DIALOG_TAG)
            }
            logger.i { "SettingsTestScreen: proceeding to change password" }
            composeUiTest.waitAssert("encryption switch is ON", encryptionSwitchSNI::assertIsOn)
            setPasswordSNI.performClick()

            changePasswordDialog {
                logger.i { "ChangePasswordDialog: Changing password with various inputs" }
                changeOldSNI.assertIsDisplayed()
                changeOldLabelSNI.assertIsDisplayed()
                changeOldVisibilitySNI.assertIsDisplayed()
                    .performClick()

                changeNewSNI.assertIsDisplayed()
                changeNewLabelSNI.assertIsDisplayed()
                changeNewVisibilitySNI.assertIsDisplayed()
                    .performClick()

                changeRepeatNewSNI.assertIsDisplayed()
                changeRepeatLabelSNI.assertIsDisplayed()
                changeRepeatNewVisibilitySNI.assertIsDisplayed()
                    .performClick()

                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("change old label has empty pass text") {
                    changeOldLabelSNI.assertTextEquals(emptyPassTitle)
                }
                changeOldSNI.performTextReplacement(text = "2")
                closeSoftKeyboard()

                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("change new label has empty pass text") {
                    changeNewLabelSNI.assertTextEquals(emptyPassTitle)
                }
                changeNewSNI.performTextReplacement(text = "2")
                closeSoftKeyboard()

                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("change repeat label has empty pass text") {
                    changeRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)
                }
                changeRepeatNewSNI.performTextReplacement(text = "2")
                closeSoftKeyboard()
                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("change repeat label has passwords do not match text") {
                    changeOldLabelSNI.assertTextEquals(incorrectPassTitle)
                }
                changeOldSNI.performTextReplacement(text = "1")
                closeSoftKeyboard()
                dialogSNI.assertIsDisplayed()
                confirmDialogButtonSNI.performClick()
                composeUiTest.awaitIdle()
                composeUiTest.waitUntilNotExist(tag = CHANGE_PASSWORD_DIALOG_TAG)
            }
            logger.i { "SettingsTestScreen: Changing password completed, proceeding to disable encryption" }
            encryptionSwitchSNI
                .assertIsOn()
                .performClick()

            composeUiTest.awaitIdle()

            enterPasswordDialog {
                logger.i { "EnterPasswordDialog: Disabling encryption with various inputs" }
                composeUiTest.waitUntilDisplayed("Enter Password Dialog", blockSNI = ::dialogSNI)
                enterVisibilitySNI.performClick()
                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("enter label has empty pass text") {
                    enterLabelSNI.assertTextEquals(emptyPassTitle)
                }
                enterPasswordSNI.performTextReplacement(text = "1")

                confirmDialogButtonSNI.performClick()
                composeUiTest.waitAssert("enter label has incorrect pass text") {
                    enterLabelSNI.assertTextEquals(incorrectPassTitle)
                }
                enterPasswordSNI.performTextReplacement(text = "2")
                confirmDialogButtonSNI.performClick()
                composeUiTest.waitUntilNotExist(tag = ENTER_PASSWORD_DIALOG_TAG)
            }
            logger.i { "SettingsTestScreen: Encryption disabled successfully" }
            composeUiTest.waitAssert("encryption switch is OFF", encryptionSwitchSNI::assertIsOff)
        }
    }
}