package com.softartdev.notedelight.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.softartdev.notedelight.ui.signin.SIGN_IN_PASSWORD_LABEL_TAG
import com.softartdev.notedelight.ui.signin.SIGN_IN_PASSWORD_FIELD_TAG
import com.softartdev.notedelight.ui.signin.SIGN_IN_PASSWORD_VISIBILITY_TAG
import com.softartdev.notedelight.util.runBlockingAll
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.settings
import notedelight.ui.shared.generated.resources.sign_in
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmInline

@JvmInline
value class SignInScreen(val nodeProvider: SemanticsNodeInteractionsProvider) {

    val settingsButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithContentDescription(runBlockingAll { getString(Res.string.settings) })
            .assertIsDisplayed()

    val passwordFieldSNI
        get() = nodeProvider.onNodeWithTag(SIGN_IN_PASSWORD_FIELD_TAG, useUnmergedTree = true)

    val passwordLabelSNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(testTag = SIGN_IN_PASSWORD_LABEL_TAG, useUnmergedTree = true)

    val passwordVisibilitySNI: SemanticsNodeInteraction
        get() = nodeProvider.onNodeWithTag(testTag = SIGN_IN_PASSWORD_VISIBILITY_TAG, useUnmergedTree = true)

    val signInButtonSNI: SemanticsNodeInteraction
        get() = nodeProvider
            .onNodeWithText(text = runBlockingAll { getString(Res.string.sign_in) })
            .assertIsDisplayed()
}