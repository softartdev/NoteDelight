package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed

class SettingPasswordTestCase(
    composeTestRule: ComposeContentTestRule,
    private val closeSoftKeyboard: () -> Unit,
) : () -> Unit, BaseTestCase(composeTestRule) {

    override fun invoke() {
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()

            settingsTestScreen {
                encryptionSwitchSNI.assertIsOff()
                    .performClick()

                confirmPasswordDialog {
                    confirmPasswordSNI.assertIsDisplayed()
                        .performClick()
                    confirmLabelSNI.assertIsDisplayed()
                    confirmRepeatPasswordSNI.assertIsDisplayed()
                    confirmRepeatLabelSNI.assertIsDisplayed()

                    confirmVisibilitySNI.assertIsDisplayed()
                        .performClick()
                    confirmRepeatVisibilitySNI.assertIsDisplayed()
                        .performClick()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        confirmLabelSNI.assertTextEquals(MR.strings.empty_password.contextLocalized())
                    }
                    confirmPasswordSNI.performTextReplacement(text = "1")
                    closeSoftKeyboard()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        confirmRepeatLabelSNI.assertTextEquals(MR.strings.passwords_do_not_match.contextLocalized())
                    }
                    confirmRepeatPasswordSNI.performTextReplacement(text = "2")

                    yesDialogButtonSNI.performClick()
                    confirmRepeatLabelSNI.assertTextEquals(MR.strings.passwords_do_not_match.contextLocalized())

                    confirmRepeatPasswordSNI.performTextReplacement(text = "1")
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOn)
                setPasswordSNI.performClick()

                changePasswordDialog {
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
                    composeTestRule.waitAssert {
                        changeOldLabelSNI.assertTextEquals(MR.strings.empty_password.contextLocalized())
                    }
                    changeOldSNI.performTextReplacement(text = "2")
                    closeSoftKeyboard()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        changeNewLabelSNI.assertTextEquals(MR.strings.empty_password.contextLocalized())
                    }
                    changeNewSNI.performTextReplacement(text = "2")
                    closeSoftKeyboard()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        changeRepeatLabelSNI.assertTextEquals(MR.strings.passwords_do_not_match.contextLocalized())
                    }
                    changeRepeatNewSNI.performTextReplacement(text = "2")
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        changeOldLabelSNI.assertTextEquals(MR.strings.incorrect_password.contextLocalized())
                    }
                    changeOldSNI.performTextReplacement(text = "1")
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                }
                encryptionSwitchSNI.assertIsOn()
                    .performClick()
                enterPasswordDialog {
                    enterPasswordSNI.assertIsDisplayed()
                    enterLabelSNI.assertIsDisplayed()
                    enterVisibilitySNI.assertIsDisplayed()
                        .performClick()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        enterLabelSNI.assertTextEquals(MR.strings.empty_password.contextLocalized())
                    }
                    enterPasswordSNI.performTextReplacement(text = "1")

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        enterLabelSNI.assertTextEquals(MR.strings.incorrect_password.contextLocalized())
                    }
                    enterPasswordSNI.performTextReplacement(text = "2")
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOff)
            }
        }
    }
}