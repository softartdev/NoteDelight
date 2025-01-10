package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.retryUntilDisplayed
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.empty_password
import notedelight.shared.generated.resources.incorrect_password
import notedelight.shared.generated.resources.passwords_do_not_match
import org.jetbrains.compose.resources.getString

class SettingPasswordTestCase(
    composeTestRule: ComposeContentTestRule,
    private val closeSoftKeyboard: () -> Unit,
) : () -> Unit, BaseTestCase(composeTestRule) {

    override fun invoke() = runTest {
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()

            settingsTestScreen {
                val emptyPassTitle = runBlocking { getString(Res.string.empty_password) }
                val passDoNotMatchTitle = runBlocking { getString(Res.string.passwords_do_not_match) }
                val incorrectPassTitle = runBlocking { getString(Res.string.incorrect_password) }

                encryptionSwitchSNI.assertIsOff()
                    .performClick()

                confirmPasswordDialog {
                    composeTestRule.retryUntilDisplayed(
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
                    composeTestRule.waitAssert {
                        confirmLabelSNI.assertTextEquals(emptyPassTitle)
                    }
                    confirmPasswordSNI.performTextReplacement(text = "1")
                    closeSoftKeyboard()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        confirmRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)
                    }
                    confirmRepeatPasswordSNI.performTextReplacement(text = "2")

                    yesDialogButtonSNI.performClick()
                    confirmRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)

                    confirmRepeatPasswordSNI.performTextReplacement(text = "1")
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOn)
                composeTestRule.waitUntilDisplayed(blockSNI = ::setPasswordSNI)
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
                        changeOldLabelSNI.assertTextEquals(emptyPassTitle)
                    }
                    changeOldSNI.performTextReplacement(text = "2")
                    closeSoftKeyboard()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        changeNewLabelSNI.assertTextEquals(emptyPassTitle)
                    }
                    changeNewSNI.performTextReplacement(text = "2")
                    closeSoftKeyboard()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        changeRepeatLabelSNI.assertTextEquals(passDoNotMatchTitle)
                    }
                    changeRepeatNewSNI.performTextReplacement(text = "2")
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        changeOldLabelSNI.assertTextEquals(incorrectPassTitle)
                    }
                    changeOldSNI.performTextReplacement(text = "1")
                    closeSoftKeyboard()
                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert(yesDialogButtonSNI::assertIsNotDisplayed)
                }
                encryptionSwitchSNI.assertIsOn()
                    .performClick()
                enterPasswordDialog {
                    composeTestRule.retryUntilDisplayed(
                        action = encryptionSwitchSNI::performClick,
                        sni = enterPasswordSNI
                    )
                    enterLabelSNI.assertIsDisplayed()
                    enterVisibilitySNI.assertIsDisplayed()
                        .performClick()

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        enterLabelSNI.assertTextEquals(emptyPassTitle)
                    }
                    enterPasswordSNI.performTextReplacement(text = "1")

                    yesDialogButtonSNI.performClick()
                    composeTestRule.waitAssert {
                        enterLabelSNI.assertTextEquals(incorrectPassTitle)
                    }
                    enterPasswordSNI.performTextReplacement(text = "2")
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule.waitAssert(encryptionSwitchSNI::assertIsOff)
            }
        }
    }
}