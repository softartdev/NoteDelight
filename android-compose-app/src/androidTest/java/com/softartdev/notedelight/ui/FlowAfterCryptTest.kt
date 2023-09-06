package com.softartdev.notedelight.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.shared.R
import com.softartdev.notedelight.shared.base.IdlingResource
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FlowAfterCryptTest {

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val password = "password"

    private val switchSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithTag(context.getString(R.string.pref_title_enable_encryption))
            .assertIsToggleable()
            .assertIsDisplayed()

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
    fun flowAfterCryptTest() {
        //main
        composeTestRule
            .onNodeWithContentDescription(label = MR.strings.settings.getString(context))
            .assertIsDisplayed()
            .performClick()
        //settings
        switchSNI.assertIsOff()

        val (_, _, confirmFieldTag) = MR.strings.enter_password.descTagTriple()
        val (_, _, confirmRepeatFieldTag) = MR.strings.confirm_password.descTagTriple()

        composeTestRule.safeWaitUntil {
            switchSNI.performClick()
            composeTestRule.onNodeWithTag(confirmFieldTag, useUnmergedTree = true)
                .assertIsDisplayed()
        }
        val confirmPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(confirmFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()
        confirmPasswordSNI.performTextReplacement(text = password)
        Espresso.closeSoftKeyboard()

        val confirmRepeatPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(confirmRepeatFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()
        confirmRepeatPasswordSNI.performTextReplacement(text = password)
        Espresso.closeSoftKeyboard()

        val confirmYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
        confirmYesSNI.performClick()
        composeTestRule.safeWaitUntil(block = switchSNI::assertIsOn)
        switchSNI.assertIsOn()

        pressBack()
        //main
        val fabSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithContentDescription(label = context.getString(R.string.create_note))
            .assertIsDisplayed()
        fabSNI.performClick()
        //note
        val textFieldSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.type_text))
            .assertIsDisplayed()
        val titleText = "Lorem"
        textFieldSNI.performTextInput(titleText)
        Espresso.closeSoftKeyboard()

        pressBack()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()

        //main
        composeTestRule.safeWaitUntil {
            composeTestRule.onNodeWithContentDescription(label = titleText).assertIsDisplayed()
        }
        composeTestRule.onNodeWithContentDescription(label = titleText).assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(label = MR.strings.settings.getString(context))
            .assertIsDisplayed()
            .performClick()
        //settings
        switchSNI.assertIsOn()
        switchSNI.performClick()

        val (_, _, enterFieldTag) = MR.strings.enter_password.descTagTriple()

        val enterPasswordSNI = composeTestRule
            .onNodeWithTag(enterFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()
        enterPasswordSNI.performTextReplacement(text = password)
        Espresso.closeSoftKeyboard()

        composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.safeWaitUntil(block = switchSNI::assertIsOff)
        switchSNI.assertIsOff()

        pressBack()
        //main
        composeTestRule.safeWaitUntil {
            composeTestRule.onNodeWithContentDescription(label = titleText).assertIsDisplayed()
        }
        composeTestRule.onNodeWithContentDescription(label = titleText)
            .assertIsDisplayed()
            .performClick()
        //note
        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.action_delete_note))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.safeWaitUntil {
            composeTestRule.onNodeWithText(text = context.getString(R.string.yes))
                .assertIsDisplayed()
        }
        composeTestRule.onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()
        //main
        composeTestRule.safeWaitUntil {
            composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
                .assertIsDisplayed()
        }
        composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
            .assertIsDisplayed()
    }
}