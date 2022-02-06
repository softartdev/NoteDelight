package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.passwordLabelTag
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SettingsPasswordTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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
    fun settingPasswordTest() {
        composeTestRule.onNodeWithContentDescription(label = MR.strings.settings.contextLocalized())
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()
            .performClick()

        val confirmPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_password))
            .assertIsDisplayed()
            .performClick()

        val confirmLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onAllNodesWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .filterToOne(hasTextExactly(context.getString(R.string.enter_password)))
            .assertIsDisplayed()

        confirmPasswordSNI.togglePasswordVisibility()

        val confirmRepeatPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.confirm_password))
            .assertIsDisplayed()

        val confirmRepeatLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onAllNodesWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .filterToOne(hasTextExactly(context.getString(R.string.confirm_password)))
            .assertIsDisplayed()

        confirmRepeatPasswordSNI.togglePasswordVisibility()

        val confirmYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()

        confirmYesSNI.performClick()

        confirmLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        confirmPasswordSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()

        confirmYesSNI.performClick()
        composeTestRule.waitForIdle()

        confirmRepeatLabelSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match))

        confirmRepeatPasswordSNI.performTextReplacement(text = "2")
        confirmYesSNI.performClick()

        confirmRepeatLabelSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match))

        confirmRepeatPasswordSNI.performTextReplacement(text = "1")
        confirmYesSNI.performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        composeTestRule.onNodeWithText(text = MR.strings.pref_title_set_password.contextLocalized())
            .assertIsDisplayed()
            .performClick()

        val changeOldSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_old_password))
            .assertIsDisplayed()

        val changeOldLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onAllNodesWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .filterToOne(hasTextExactly(context.getString(R.string.enter_old_password)))
            .assertIsDisplayed()

        val changeNewSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_new_password))
            .assertIsDisplayed()

        val changeNewLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onAllNodesWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .filterToOne(hasTextExactly(context.getString(R.string.enter_new_password)))
            .assertIsDisplayed()

        val changeRepeatNewSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.repeat_new_password))
            .assertIsDisplayed()

        val changeRepeatLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onAllNodesWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .filterToOne(hasTextExactly(context.getString(R.string.repeat_new_password)))
            .assertIsDisplayed()

        changeOldSNI.togglePasswordVisibility()

        val changeYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        changeOldLabelSNI.assertTextEquals(context.getString(R.string.empty_password))
        changeOldSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        changeNewSNI.onChild().performClick() // toggle password visibility

        changeYesSNI.performClick()
        composeTestRule.waitForIdle()

        changeNewLabelSNI.assertTextEquals(context.getString(R.string.empty_password))
        changeNewSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        changeRepeatNewSNI.togglePasswordVisibility()

        changeYesSNI.performClick()

        changeRepeatLabelSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match))

        changeRepeatNewSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        changeYesSNI.performClick()

        changeOldLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))
        changeOldSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()

        changeYesSNI.performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
            .performClick()

        val enterPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_password))
            .assertIsDisplayed()

        val enterLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onAllNodesWithTag(testTag = passwordLabelTag, useUnmergedTree = true)
            .filterToOne(hasTextExactly(context.getString(R.string.enter_password)))
            .assertIsDisplayed()

        enterPasswordSNI.togglePasswordVisibility()

        val enterYesSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()

        enterLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        enterPasswordSNI.performTextReplacement(text = "1")

        enterYesSNI.performClick()

        enterLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))

        enterPasswordSNI.performTextReplacement(text = "2")

        enterYesSNI.performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()

        composeTestRule.onAllNodes(isRoot()).printToLog("🦄", maxDepth = Int.MAX_VALUE)
    }
}