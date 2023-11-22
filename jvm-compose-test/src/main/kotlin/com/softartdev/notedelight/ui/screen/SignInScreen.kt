package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.descTagTriple

@JvmInline
value class SignInScreen(val composeTestRule: ComposeContentTestRule) {

    val passwordFieldSNI
        get() = composeTestRule.onNodeWithTag(enterFieldTag, useUnmergedTree = true)

    val passwordLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(testTag = enterLabelTag, useUnmergedTree = true)

    val passwordVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(testTag = enterVisibilityTag, useUnmergedTree = true)

    val signInButtonSNI: SemanticsNodeInteraction
        get() = composeTestRule
            .onNodeWithText(text = MR.strings.sign_in.contextLocalized())
            .assertIsDisplayed()

    companion object {
        private val enterTags = MR.strings.enter_password.descTagTriple()
        private val enterLabelTag = enterTags.first
        private val enterVisibilityTag = enterTags.second
        private val enterFieldTag = enterTags.third
    }
}