package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.ui.test.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.base.IdlingResource
import com.softartdev.notedelight.shared.test.util.Encryptor
import com.softartdev.notedelight.ui.descTagTriple
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

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(IdlingResource.countingIdlingResource)
        composeTestRule.registerIdlingResource(composeIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        composeTestRule.unregisterIdlingResource(composeIdlingResource)
        IdlingRegistry.getInstance().unregister(IdlingResource.countingIdlingResource)
    }

    @Test
    fun signInTest() {
        composeTestRule.onAllNodes(isRoot(), useUnmergedTree = true)
            .printToLog("🦄", maxDepth = Int.MAX_VALUE)

        val (enterLabelTag, enterVisibilityTag, enterFieldTag) = MR.strings.enter_password.descTagTriple()

        val passwordFieldSNI = composeTestRule
            .onNodeWithTag(enterFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()

        val passwordLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(testTag = enterLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(context.getString(R.string.enter_password))

        composeTestRule.togglePasswordVisibility(enterVisibilityTag)

        val signInButtonSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.sign_in))
            .assertIsDisplayed()

        composeTestRule.advancePerform(signInButtonSNI::performClick)

        passwordLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        passwordFieldSNI.performTextReplacement(text = "incorrect password")
        Espresso.closeSoftKeyboard()
        composeTestRule.advancePerform(signInButtonSNI::performClick)

        passwordLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))

        passwordFieldSNI.performTextReplacement(text = Encryptor.PASSWORD)
        Espresso.closeSoftKeyboard()
        composeTestRule.advancePerform(signInButtonSNI::performClick)

        composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
            .assertIsDisplayed()
    }
}