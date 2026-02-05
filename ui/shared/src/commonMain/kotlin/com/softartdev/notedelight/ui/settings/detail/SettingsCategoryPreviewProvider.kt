package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.softartdev.notedelight.model.SettingsCategory

class SettingsCategoryPreviewProvider(
    override val values: Sequence<SettingsCategory> = SettingsCategory.entries.asSequence()
) : PreviewParameterProvider<SettingsCategory>