package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.ui.descTagTriple
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.enter_new_password
import notedelight.shared.generated.resources.enter_old_password
import notedelight.shared.generated.resources.repeat_new_password

@JvmInline
value class ChangePasswordDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val changeOldSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeOldFieldTag, useUnmergedTree = true)

    val changeOldLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeOldLabelTag, useUnmergedTree = true)

    val changeOldVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeOldVisibilityTag, useUnmergedTree = true)

    val changeNewSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeNewFieldTag, useUnmergedTree = true)

    val changeNewLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeNewLabelTag, useUnmergedTree = true)

    val changeNewVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeNewVisibilityTag, useUnmergedTree = true)

    val changeRepeatNewSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeRepeatNewFieldTag, useUnmergedTree = true)

    val changeRepeatLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeRepeatNewLabelTag, useUnmergedTree = true)

    val changeRepeatNewVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(changeRepeatNewVisibilityTag, useUnmergedTree = true)

    companion object {
        private val changeOldTags = Res.string.enter_old_password.descTagTriple()
        private val changeOldLabelTag = changeOldTags.first
        private val changeOldVisibilityTag = changeOldTags.second
        private val changeOldFieldTag = changeOldTags.third

        private val changeNewTags = Res.string.enter_new_password.descTagTriple()
        private val changeNewLabelTag = changeNewTags.first
        private val changeNewVisibilityTag = changeNewTags.second
        private val changeNewFieldTag = changeNewTags.third

        private val changeRepeatTags = Res.string.repeat_new_password.descTagTriple()
        private val changeRepeatNewLabelTag = changeRepeatTags.first
        private val changeRepeatNewVisibilityTag = changeRepeatTags.second
        private val changeRepeatNewFieldTag = changeRepeatTags.third
    }
}