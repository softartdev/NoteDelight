package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.base.IdlingResource
import com.softartdev.notedelight.ui.descTagTriple
import org.junit.*
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FlowAfterCryptTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val password = "password"

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

    @Ignore
    @Test
    fun flowAfterCryptTest() {
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

        val settingsSNI = composeTestRule
            .onNodeWithContentDescription(label = MR.strings.settings.contextLocalized())
            .assertIsDisplayed()
        settingsSNI.performClick()

        //settings
        val switchSNI = composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.pref_title_enable_encryption))
            .assertIsToggleable()
            .assertIsDisplayed()
        switchSNI.assertIsOff()
        switchSNI.performClick()

        val (confirmLabelTag, confirmVisibilityTag, confirmFieldTag) = MR.strings.enter_password.descTagTriple()
        val (confirmRepeatLabelTag, confirmRepeatVisibilityTag, confirmRepeatFieldTag) = MR.strings.confirm_password.descTagTriple()

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

        switchSNI.assertIsOn()

        composeTestRule.onRoot().printToLog("🦄", maxDepth = Int.MAX_VALUE)

        composeTestRule.onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
        //note
        composeTestRule.onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()
        //main
        val listItemSNI = composeTestRule.onNodeWithContentDescription(label = titleText)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(label = MR.strings.settings.contextLocalized())
            .assertIsDisplayed()
            .performClick()
        //settings
        switchSNI.assertIsOn()
        switchSNI.performClick()

        val (enterLabelTag, enterVisibilityTag, enterFieldTag) = MR.strings.enter_password.descTagTriple()

        val enterPasswordSNI = composeTestRule
            .onNodeWithTag(enterFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()
        enterPasswordSNI.performTextReplacement(text = password)
        Espresso.closeSoftKeyboard()

        composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()

        switchSNI.assertIsOff()

        composeTestRule.onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
        //main
        listItemSNI.performClick()
        //note
        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.action_delete_note))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()
        //main
        composeTestRule.onNodeWithText(text = context.getString(R.string.label_empty_result))
            .assertIsDisplayed()
    }
}