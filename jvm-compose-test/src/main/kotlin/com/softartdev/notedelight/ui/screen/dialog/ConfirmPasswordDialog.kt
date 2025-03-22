package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.descTagTriple
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.confirm_password
import notedelight.shared.generated.resources.enter_password

@JvmInline
value class ConfirmPasswordDialog(val commonDialog: CommonDialog): CommonDialog by commonDialog {

    val confirmPasswordSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(confirmFieldTag, useUnmergedTree = true)

    val confirmLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(confirmLabelTag, useUnmergedTree = true)

    val confirmVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(confirmVisibilityTag, useUnmergedTree = true)

    val confirmRepeatPasswordSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(confirmRepeatFieldTag, useUnmergedTree = true)

    val confirmRepeatLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(confirmRepeatLabelTag, useUnmergedTree = true)

    val confirmRepeatVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(confirmRepeatVisibilityTag, useUnmergedTree = true)

    companion object {
        private val confirmTags = Res.string.enter_password.descTagTriple()
        private val confirmLabelTag = confirmTags.first
        private val confirmVisibilityTag = confirmTags.second
        private val confirmFieldTag = confirmTags.third

        private val confirmRepeatTags = Res.string.confirm_password.descTagTriple()
        private val confirmRepeatLabelTag = confirmRepeatTags.first
        private val confirmRepeatVisibilityTag = confirmRepeatTags.second
        private val confirmRepeatFieldTag = confirmRepeatTags.third
    }
}