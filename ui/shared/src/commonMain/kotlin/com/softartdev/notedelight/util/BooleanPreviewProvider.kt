package com.softartdev.notedelight.util

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class BooleanPreviewProvider(
    override val values: Sequence<Boolean> = sequenceOf(false, true)
) : PreviewParameterProvider<Boolean>
