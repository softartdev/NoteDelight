@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.empty_password
import notedelight.ui.shared.generated.resources.enter_password
import notedelight.ui.shared.generated.resources.incorrect_password
import org.jetbrains.compose.resources.getString

class SignInTestCase(
    composeUiTest: ComposeUiTest,
    private val closeSoftKeyboard: () -> Unit,
) : () -> TestResult, BaseTestCase(composeUiTest) {

    override fun invoke() = runTest {
        signInScreen {
            composeUiTest.waitUntilDisplayed("passwordField", blockSNI = ::passwordFieldSNI)

            passwordLabelSNI.assertIsDisplayed()
                .assertTextEquals(getString(Res.string.enter_password))

            passwordVisibilitySNI.assertIsDisplayed()
                .performClick()

            signInButtonSNI.performClick()
            val emptyPassTitle = getString(Res.string.empty_password)
            composeUiTest.waitAssert("password label has empty pass text") {
                passwordLabelSNI.assertTextEquals(emptyPassTitle)
            }
            passwordFieldSNI.performTextReplacement(text = "incorrect password")
            closeSoftKeyboard()
            signInButtonSNI.performClick()

            passwordLabelSNI.assertTextEquals(getString(Res.string.incorrect_password))

            passwordFieldSNI.performTextReplacement(text = DbTestEncryptor.PASSWORD)
            closeSoftKeyboard()
            signInButtonSNI.performClick()

            mainTestScreen {
                composeUiTest.waitUntilDisplayed("emptyResultLabel", blockSNI = ::emptyResultLabelSNI)
            }
        }
    }
}