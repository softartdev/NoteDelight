package com.softartdev.notedelight.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.presentation.settings.LanguageViewModel
import com.softartdev.notedelight.util.stringResource
import com.softartdev.notedelight.util.testTag
import io.github.softartdev.theme_prefs.generated.resources.ok
import io.github.softartdev.theme_prefs.generated.resources.Res as ThemePrefsRes
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.choose_language
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LanguageDialog(languageViewModel: LanguageViewModel) {
    val selectedLanguage: LanguageEnum by languageViewModel.selectedLanguage.collectAsState()
    LanguageDialogBody(
        selectedLanguage = selectedLanguage,
        onLanguageSelected = languageViewModel::selectLanguage,
        onDismiss = languageViewModel::dismiss
    )
}

@Composable
fun LanguageDialogBody(
    selectedLanguage: LanguageEnum,
    onLanguageSelected: (LanguageEnum) -> Unit,
    onDismiss: () -> Unit
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.choose_language)) },
    text = {
        Column(Modifier.selectableGroup()) {
            LanguageEnum.entries.forEach { language: LanguageEnum ->
                Row(
                    modifier = Modifier
                        .testTag(tag = language.testTag)
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = language == selectedLanguage,
                            onClick = { onLanguageSelected(language) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = language == selectedLanguage,
                        onClick = null
                    )
                    Text(
                        text = stringResource(language.stringResource),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    },
    confirmButton = { Button(onClick = onDismiss) { Text(stringResource(ThemePrefsRes.string.ok)) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
private fun LanguageDialogBodyPreview() {
    LanguageDialogBody(
        selectedLanguage = LanguageEnum.ENGLISH,
        onLanguageSelected = {},
        onDismiss = {}
    )
}
