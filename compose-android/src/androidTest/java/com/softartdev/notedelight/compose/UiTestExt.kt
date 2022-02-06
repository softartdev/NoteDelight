package com.softartdev.notedelight.compose

import androidx.compose.ui.test.*
import com.softartdev.notedelight.ui.passwordVisibilityDesc


fun SemanticsNodeInteraction.togglePasswordVisibility() = onChildren()
    .filterToOne(hasContentDescription(passwordVisibilityDesc))
    .performClick()
