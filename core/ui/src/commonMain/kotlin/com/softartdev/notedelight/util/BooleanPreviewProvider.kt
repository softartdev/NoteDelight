package com.softartdev.notedelight.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class BooleanPreviewProvider(
    override val values: Sequence<Boolean> = sequenceOf(false, true)
) : PreviewParameterProvider<Boolean>
