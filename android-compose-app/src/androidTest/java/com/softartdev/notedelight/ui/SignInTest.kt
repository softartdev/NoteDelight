package com.softartdev.notedelight.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.base.IdlingResource
import com.softartdev.notedelight.shared.test.util.Encryptor
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SignInTest {

    private val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = Encryptor::encryptDB
    )

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

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

        composeTestRule.safeWaitUntil {
            signInButtonSNI.performClick()
            passwordLabelSNI.assertTextEquals(context.getString(R.string.empty_password))
        }
        passwordLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        passwordFieldSNI.performTextReplacement(text = "incorrect password")
        Espresso.closeSoftKeyboard()
        composeTestRule.advancePerform(signInButtonSNI::performClick)

        passwordLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))

        passwordFieldSNI.performTextReplacement(text = Encryptor.PASSWORD)
        Espresso.closeSoftKeyboard()
        composeTestRule.advancePerform(signInButtonSNI::performClick)
        composeTestRule.safeWaitUntil {
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
                .assertIsDisplayed()
        }
        composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
            .assertIsDisplayed()
    }
}