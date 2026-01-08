package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.util.SIGN_IN_BUTTON_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_FIELD_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_LABEL_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_VISIBILITY_TAG
import com.softartdev.notedelight.util.SIGN_IN_SETTINGS_BUTTON_TAG
import kotlin.jvm.JvmInline

@JvmInline
value class SignInScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val settingsButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SIGN_IN_SETTINGS_BUTTON_TAG)
            .assertIsDisplayed()

    val passwordFieldSNI
        get() = nodeProvider.onNodeWithTag(SIGN_IN_PASSWORD_FIELD_TAG, useUnmergedTree = true)

    val passwordLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(testTag = SIGN_IN_PASSWORD_LABEL_TAG, useUnmergedTree = true)

    val passwordVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(testTag = SIGN_IN_PASSWORD_VISIBILITY_TAG, useUnmergedTree = true)

    val signInButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithTag(SIGN_IN_BUTTON_TAG)
            .assertIsDisplayed()
}