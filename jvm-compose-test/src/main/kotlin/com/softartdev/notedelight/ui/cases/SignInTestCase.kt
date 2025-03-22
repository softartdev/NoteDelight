package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.empty_password
import notedelight.shared.generated.resources.enter_password
import notedelight.shared.generated.resources.incorrect_password
import org.jetbrains.compose.resources.getString

class SignInTestCase(
    composeTestRule: ComposeContentTestRule,
    private val closeSoftKeyboard: () -> Unit,
) : () -> Unit, BaseTestCase(composeTestRule) {

    override fun invoke() = runTest {
        signInScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::passwordFieldSNI)

            passwordLabelSNI.assertIsDisplayed()
                .assertTextEquals(runBlocking { getString(Res.string.enter_password) })

            passwordVisibilitySNI.assertIsDisplayed()
                .performClick()

            signInButtonSNI.performClick()
            val emptyPassTitle = runBlocking { getString(Res.string.empty_password) }
            composeTestRule.waitAssert {
                passwordLabelSNI.assertTextEquals(emptyPassTitle)
            }
            passwordFieldSNI.performTextReplacement(text = "incorrect password")
            closeSoftKeyboard()
            signInButtonSNI.performClick()

            passwordLabelSNI.assertTextEquals(runBlocking { getString(Res.string.incorrect_password) })

            passwordFieldSNI.performTextReplacement(text = DbTestEncryptor.PASSWORD)
            closeSoftKeyboard()
            signInButtonSNI.performClick()

            mainTestScreen {
                composeTestRule.waitUntilDisplayed(blockSNI = ::emptyResultLabelSNI)
            }
        }
    }
}