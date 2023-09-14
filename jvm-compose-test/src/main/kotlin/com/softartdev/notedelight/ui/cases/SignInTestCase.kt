package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitUntilDisplayed

class SignInTestCase(
    composeTestRule: ComposeContentTestRule,
    private val closeSoftKeyboard: () -> Unit,
) : () -> Unit, BaseTestCase(composeTestRule) {

    override fun invoke() {
        signInScreen {
            passwordFieldSNI.assertIsDisplayed()

            passwordLabelSNI.assertIsDisplayed()
                .assertTextEquals(MR.strings.enter_password.contextLocalized())

            passwordVisibilitySNI.assertIsDisplayed()
                .performClick()

            signInButtonSNI.performClick()
            passwordLabelSNI.assertTextEquals(MR.strings.empty_password.contextLocalized())

            passwordFieldSNI.performTextReplacement(text = "incorrect password")
            closeSoftKeyboard()
            signInButtonSNI.performClick()

            passwordLabelSNI.assertTextEquals(MR.strings.incorrect_password.contextLocalized())

            passwordFieldSNI.performTextReplacement(text = DbTestEncryptor.PASSWORD)
            closeSoftKeyboard()
            signInButtonSNI.performClick()

            mainTestScreen {
                composeTestRule.waitUntilDisplayed(blockSNI = ::emptyResultLabelSNI)
            }
        }
    }
}