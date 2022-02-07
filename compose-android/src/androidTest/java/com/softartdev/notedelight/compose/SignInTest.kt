package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.ui.test.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.shared.test.util.Encryptor
import com.softartdev.notedelight.ui.passwordLabelTag
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SignInTest {

    @get:Rule
    val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = Encryptor::encryptDB
    )

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val countingIdlingResource =
        com.softartdev.notedelight.shared.base.IdlingResource.countingIdlingResource

    private val composeIdlingResource = object : IdlingResource {
        override val isIdleNow: Boolean
            get() = countingIdlingResource.isIdleNow
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(countingIdlingResource)
        composeTestRule.registerIdlingResource(composeIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
        composeTestRule.unregisterIdlingResource(composeIdlingResource)
    }

    @Test
    fun signInTest() {
        composeTestRule.onAllNodes(isRoot(), useUnmergedTree = true)
            .printToLog("🦄", maxDepth = Int.MAX_VALUE)

        val passwordFieldSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_password))
            .assertIsDisplayed()

        val passwordLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(context.getString(R.string.enter_password))

        passwordFieldSNI.togglePasswordVisibility()

        val signInButtonSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.sign_in))
            .assertIsDisplayed()
            .performClick()

        passwordLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        passwordFieldSNI.performTextReplacement(text = "incorrect password")
        Espresso.closeSoftKeyboard()
        signInButtonSNI.performClick()

        passwordLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))

        passwordFieldSNI.performTextReplacement(text = Encryptor.PASSWORD)
        Espresso.closeSoftKeyboard()
        signInButtonSNI.performClick()

        composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
            .assertIsDisplayed()
    }
}