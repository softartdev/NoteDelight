package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.retryUntilDisplayed
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.empty_password
import notedelight.ui.shared.generated.resources.incorrect_password
import notedelight.ui.shared.generated.resources.passwords_do_not_match
import org.jetbrains.compose.resources.getString

class SettingPasswordTestCase(
    composeTestRule: ComposeContentTestRule,
    private val closeSoftKeyboard: () -> Unit,
) : () -> Unit, BaseTestCase(composeTestRule) {
    private val logger = Logger.withTag("ℹ️SettingPasswordTestCase")

    override fun invoke() = runTest {
        logger.i { "Starting SettingPasswordTestCase" }
        val emptyPassTitle = getString(Res.string.empty_password)
        val passDoNotMatchTitle = getString(Res.string.passwords_do_not_match)
        val incorrectPassTitle = getString(Res.string.incorrect_password)
        mainTestScreen {
            logger.i { "MainTestScreen: Opening settings screen" }
            composeTestRule.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
        }
        settingsTestScreen {
            logger.i { "SettingsTestScreen: Toggling encryption switch ON" }
            encryptionSwitchSNI.assertIsOff()
                .performClick()

            confirmPasswordDialog {
                logger.i { "ConfirmPasswordDialog: Setting password with various inputs" }
                retryUntilDisplayed(
                    description = "Confirm Password Field",
                    action = encryptionSwitchSNI::performClick,
                    sni = confirmPasswordSNI
                )
                confirmPasswordSNI.performClick()
                confirmLabelSNI.assertIsDisplayed()
                confirmRepeatPasswordSNI.assertIsDisplayed()
                confirmRepeatLabelSNI.assertIsDisplayed()

                confirmVisibilitySNI.assertIsDisplayed()
                    .performClick()
                confirmRepeatVisibilitySNI.assertIsDisplayed()
                    .performClick()

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("confirm label has empty pass text") {
                    confirmLabelSNI.assertTextEquals(emptyPassTitle)
                }
                confirmPasswordSNI.performTextReplacement(text = "1")
                closeSoftKeyboard()

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("confirm repeat label has empty pass text") {
                    confirmRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)
                }
                confirmRepeatPasswordSNI.performTextReplacement(text = "2")

                yesDialogButtonSNI.performClick()
                confirmRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)

                confirmRepeatPasswordSNI.performTextReplacement(text = "1")
                yesDialogButtonSNI.performClick()
            }
            logger.i { "SettingsTestScreen: Encryption switch is ON, proceeding to change password" }
            composeTestRule.waitAssert("encryption switch is ON", encryptionSwitchSNI::assertIsOn)
            composeTestRule.waitUntilDisplayed("setPassword", blockSNI = ::setPasswordSNI)
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

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("change old label has empty pass text") {
                    changeOldLabelSNI.assertTextEquals(emptyPassTitle)
                }
                changeOldSNI.performTextReplacement(text = "2")
                closeSoftKeyboard()

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("change new label has empty pass text") {
                    changeNewLabelSNI.assertTextEquals(emptyPassTitle)
                }
                changeNewSNI.performTextReplacement(text = "2")
                closeSoftKeyboard()

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("change repeat label has empty pass text") {
                    changeRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)
                }
                changeRepeatNewSNI.performTextReplacement(text = "2")
                closeSoftKeyboard()
                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("change repeat label has passwords do not match text") {
                    changeOldLabelSNI.assertTextEquals(incorrectPassTitle)
                }
                changeOldSNI.performTextReplacement(text = "1")
                closeSoftKeyboard()
                yesDialogButtonSNI.performClick()
            }
            logger.i { "SettingsTestScreen: Changing password completed, proceeding to disable encryption" }
            encryptionSwitchSNI.assertIsOn()
                .performClick()
            enterPasswordDialog {
                logger.i { "EnterPasswordDialog: Disabling encryption with various inputs" }
                retryUntilDisplayed(
                    description = "Enter Password Field",
                    action = encryptionSwitchSNI::performClick,
                    sni = enterPasswordSNI
                )
                logger.i { "EnterPasswordDialog: enterPasswordSNI is shown" }
                enterLabelSNI.assertIsDisplayed()
                logger.i { "EnterPasswordDialog: enterLabelSNI is displayed" }
                enterVisibilitySNI.assertIsDisplayed()
                    .performClick()

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("enter label has empty pass text") {
                    enterLabelSNI.assertTextEquals(emptyPassTitle)
                }
                enterPasswordSNI.performTextReplacement(text = "1")

                yesDialogButtonSNI.performClick()
                composeTestRule.waitAssert("enter label has incorrect pass text") {
                    enterLabelSNI.assertTextEquals(incorrectPassTitle)
                }
                enterPasswordSNI.performTextReplacement(text = "2")
                yesDialogButtonSNI.performClick()
            }
            logger.i { "SettingsTestScreen: Encryption disabled successfully" }
            composeTestRule.waitAssert("encryption switch is OFF", encryptionSwitchSNI::assertIsOff)
        }
    }
}