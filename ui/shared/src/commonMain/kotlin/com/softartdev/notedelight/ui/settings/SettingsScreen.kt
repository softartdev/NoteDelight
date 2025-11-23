@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.softartdev.notedelight.presentation.settings.SecurityResult
import com.softartdev.notedelight.presentation.settings.SettingsAction
import com.softartdev.notedelight.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.notedelight.util.createMultiplatformMessage
import com.softartdev.notedelight.util.stringResource
import com.softartdev.theme.material3.PreferableMaterialTheme
import com.softartdev.theme.material3.ThemePreferenceItem
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.language
import notedelight.ui.shared.generated.resources.pref_title_check_cipher_version
import notedelight.ui.shared.generated.resources.pref_title_enable_encryption
import notedelight.ui.shared.generated.resources.pref_title_file_list
import notedelight.ui.shared.generated.resources.pref_title_set_password
import notedelight.ui.shared.generated.resources.pref_title_show_db_path
import notedelight.ui.shared.generated.resources.security
import notedelight.ui.shared.generated.resources.settings
import notedelight.ui.shared.generated.resources.theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val result: SecurityResult by settingsViewModel.stateFlow.collectAsState()
    LifecycleResumeEffect(key1 = settingsViewModel) {
        settingsViewModel.onAction(SettingsAction.CheckEncryption)
        onPauseOrDispose {}
    }
    SettingsScreenBody(result, settingsViewModel::onAction)
}

@Composable
fun SettingsScreenBody(
    result: SecurityResult = SecurityResult(),
    onAction: (action: SettingsAction) -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.settings)) },
            navigationIcon = {
                IconButton(onClick = { onAction(SettingsAction.NavBack) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
        )
    },
    content = { paddingValues: PaddingValues ->
        val enableEncryptionPrefTitle = stringResource(Res.string.pref_title_enable_encryption)
        Column(modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())) {
            if (result.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            PreferenceCategory(stringResource(Res.string.theme), Icons.Default.Brightness4)
            ThemePreferenceItem(onClick = { onAction(SettingsAction.ChangeTheme) })
            Preference(
                title = stringResource(Res.string.language),
                vector = Icons.Default.Language,
                onClick = { onAction(SettingsAction.ChangeLanguage) },
                secondaryText = { Text(stringResource(result.language.stringResource)) }
            )
            PreferenceCategory(stringResource(Res.string.security), Icons.Default.Security)
            Preference(
                modifier = Modifier.semantics {
                    contentDescription = enableEncryptionPrefTitle
                    toggleableState = ToggleableState(result.encryption)
                    testTag = enableEncryptionPrefTitle
                },
                title = enableEncryptionPrefTitle,
                vector = Icons.Default.Lock,
                onClick = { onAction(SettingsAction.ChangeEncryption(!result.encryption)) }
            ) {
                Switch(checked = result.encryption, onCheckedChange = { onAction(SettingsAction.ChangeEncryption(it)) })
            }
            Preference(
                title = stringResource(Res.string.pref_title_set_password),
                vector = Icons.Default.Password,
                onClick = { onAction(SettingsAction.ChangePassword) }
            )
            Preference(
                title = stringResource(Res.string.pref_title_check_cipher_version),
                vector = Icons.Filled.FileLock,
                onClick = { onAction(SettingsAction.ShowCipherVersion) }
            )
            Preference(
                title = stringResource(Res.string.pref_title_show_db_path),
                vector = Icons.Default.Storage,
                onClick = { onAction(SettingsAction.ShowDatabasePath) }
            )
            if (result.fileListVisible) {
                Preference(
                    title = stringResource(Res.string.pref_title_file_list),
                    vector = Icons.Default.Folder,
                    onClick = { onAction(SettingsAction.ShowFileList) }
                )
            }
            Spacer(Modifier.height(32.dp))
            ListItem(
                modifier = Modifier.clickable { onAction(SettingsAction.RevealFileList) },
                headlineContent = {},
                supportingContent = { Text(createMultiplatformMessage()) }
            )
        }
    },
)

@Composable
fun PreferenceCategory(title: String, vector: ImageVector) = ListItem(
    leadingContent = { Icon(imageVector = vector, contentDescription = title) },
    headlineContent = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
)

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    title: String,
    vector: ImageVector,
    onClick: () -> Unit = {},
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) = ListItem(
    modifier = modifier.clickable(onClick = onClick),
    leadingContent = { Icon(imageVector = vector, contentDescription = title) },
    headlineContent = { Text(text = title) },
    supportingContent = secondaryText,
    trailingContent = trailing
)

@Preview
@Composable
fun PreviewSettingsScreenBody() = PreferableMaterialTheme {
    SettingsScreenBody()
}
