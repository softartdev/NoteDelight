package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
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

        val visibilities: SemanticsNodeInteractionCollection = composeTestRule
            .onAllNodesWithContentDescription(label = Icons.Default.Visibility.name)
            .assertCountEquals(2)
        for (i in 0..1) {
            val visibility: SemanticsNodeInteraction = visibilities[i]
            visibility.performClick()
        }
        val confirmRepeatPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.confirm_password))
            .assertIsDisplayed()

        val confirmYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()

        confirmYesSNI.performClick()
        waitUntilText(
            actualSNI = confirmPasswordSNI,
            expectedText = context.getString(R.string.empty_password),
            doOnEach = confirmYesSNI::performClick
        )
        composeTestRule.onNodeWithText(text = context.getString(R.string.empty_password))
            .assertIsDisplayed()

        confirmPasswordSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()

        confirmYesSNI.performClick()
        waitUntilText(
            actualSNI = confirmRepeatPasswordSNI,
            expectedText = context.getString(R.string.passwords_do_not_match),
            doOnEach = confirmYesSNI::performClick
        )
        composeTestRule.onNodeWithText(text = context.getString(R.string.passwords_do_not_match))
            .assertIsDisplayed()
            .performClick()

        confirmRepeatPasswordSNI.performTextReplacement(text = "2")
        confirmYesSNI.performClick()

        composeTestRule.onNodeWithText(text = context.getString(R.string.passwords_do_not_match))
            .assertIsDisplayed()

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

        val changeNewSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_new_password))
            .assertIsDisplayed()

        val changeRepeatNewSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.repeat_new_password))
            .assertIsDisplayed()

        changeOldSNI.onChild().performClick() // toggle password visibility

        val changeYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        changeOldSNI.assertTextEquals(context.getString(R.string.empty_password), includeEditableText = false)
        changeOldSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        changeNewSNI.onChild().performClick() // toggle password visibility

        changeYesSNI.performClick()
        composeTestRule.waitForIdle()

        changeNewSNI.assertTextEquals(context.getString(R.string.empty_password), includeEditableText = false)
        changeNewSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        changeRepeatNewSNI.onChild().performClick() // toggle password visibility

        changeYesSNI.performClick()
        composeTestRule.waitForIdle()
        waitUntilText(
            actualSNI = changeRepeatNewSNI,
            expectedText = context.getString(R.string.passwords_do_not_match),
            doOnEach = changeYesSNI::performClick
        )
        changeRepeatNewSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match), includeEditableText = false)
        changeRepeatNewSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        changeYesSNI.performClick()

        changeOldSNI.assertTextEquals(context.getString(R.string.incorrect_password), includeEditableText = false)
        changeOldSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()

        changeYesSNI.performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        composeTestRule.onAllNodes(isRoot()).printToLog("🦄", maxDepth = Int.MAX_VALUE)
        //TODO
    }

    private fun waitUntilText(
        actualSNI: SemanticsNodeInteraction,
        expectedText: String,
        doOnEach: (() -> Unit)? = null
    ) = composeTestRule.waitUntil {
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeByFrame()

        val config = actualSNI.fetchSemanticsNode().config
        val actualText = config.getOrNull(SemanticsProperties.Text)!!.single().text
        val res = actualText == expectedText
        if (!res) doOnEach?.invoke()
        return@waitUntil res
    }
}