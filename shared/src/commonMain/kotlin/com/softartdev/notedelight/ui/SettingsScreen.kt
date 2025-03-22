@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.coroutineScope
import com.softartdev.notedelight.presentation.settings.SecurityResult
import com.softartdev.notedelight.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.notedelight.util.createMultiplatformMessage
import com.softartdev.theme.material3.ThemePreferenceItem
import kotlinx.coroutines.launch
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.copy
import notedelight.shared.generated.resources.pref_title_check_cipher_version
import notedelight.shared.generated.resources.pref_title_enable_encryption
import notedelight.shared.generated.resources.pref_title_set_password
import notedelight.shared.generated.resources.pref_title_show_db_path
import notedelight.shared.generated.resources.security
import notedelight.shared.generated.resources.settings
import notedelight.shared.generated.resources.theme
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val result: SecurityResult by settingsViewModel.stateFlow.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    LifecycleResumeEffect(key1 = settingsViewModel, key2 = result) {
        result.checkEncryption()
        result.snackBarMessage?.takeIf(String::isNotEmpty)?.let { msg: String ->
            lifecycle.coroutineScope.launch {
                val snackResult: SnackbarResult = snackbarHostState.showSnackbar(
                    message = msg,
                    duration = SnackbarDuration.Long,
                    actionLabel = getString(Res.string.copy),
                )
                if (snackResult == ActionPerformed) clipboardManager.setText(AnnotatedString(msg))
            }
            result.disposeOneTimeEvents()
        }
        onPauseOrDispose { result.disposeOneTimeEvents() }
    }
    SettingsScreenBody(result, snackbarHostState)
}

@Composable
fun SettingsScreenBody(
    result: SecurityResult = SecurityResult(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.settings)) },
            navigationIcon = {
                IconButton(onClick = result.navBack) {
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
        Column(modifier = Modifier.padding(paddingValues)) {
            if (result.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            PreferenceCategory(stringResource(Res.string.theme), Icons.Default.Brightness4)
            ThemePreferenceItem(onClick = result.changeTheme)
            PreferenceCategory(stringResource(Res.string.security), Icons.Default.Security)
            Preference(
                modifier = Modifier.semantics {
                    contentDescription = enableEncryptionPrefTitle
                    toggleableState = ToggleableState(result.encryption)
                    testTag = enableEncryptionPrefTitle
                },
                title = enableEncryptionPrefTitle,
                vector = Icons.Default.Lock,
                onClick = { result.changeEncryption(!result.encryption) }
            ) {
                Switch(checked = result.encryption, onCheckedChange = result.changeEncryption)
            }
            Preference(
                title = stringResource(Res.string.pref_title_set_password),
                vector = Icons.Default.Password,
                onClick = result.changePassword
            )
            Preference(
                title = stringResource(Res.string.pref_title_check_cipher_version),
                vector = Icons.Filled.FileLock,
                onClick = result.showCipherVersion
            )
            Preference(
                title = stringResource(Res.string.pref_title_show_db_path),
                vector = Icons.Default.Storage,
                onClick = result.showDatabasePath
            )
            Spacer(Modifier.height(32.dp))
            ListItem(
                headlineContent = {},
                supportingContent = { Text(createMultiplatformMessage()) }
            )
        }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
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
fun PreviewSettingsScreenBody() = SettingsScreenBody()
