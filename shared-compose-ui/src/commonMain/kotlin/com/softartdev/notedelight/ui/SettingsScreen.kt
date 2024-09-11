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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.softartdev.notedelight.shared.createMultiplatformMessage
import com.softartdev.notedelight.shared.presentation.settings.SecurityResult
import com.softartdev.notedelight.shared.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.theme.material3.ThemePreferenceItem
import kotlinx.coroutines.launch
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.pref_title_check_cipher_version
import notedelight.shared_compose_ui.generated.resources.pref_title_enable_encryption
import notedelight.shared_compose_ui.generated.resources.pref_title_set_password
import notedelight.shared_compose_ui.generated.resources.security
import notedelight.shared_compose_ui.generated.resources.settings
import notedelight.shared_compose_ui.generated.resources.theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val result: SecurityResult by settingsViewModel.stateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    LifecycleResumeEffect(key1 = settingsViewModel, key2 = result ) {
        result.checkEncryption()
        result.snackBarMessage?.takeIf(String::isNotEmpty)?.let { msg: String ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message = msg) }
            result.disposeOneTimeEvents()
        }
        onPauseOrDispose { result.checkEncryption() }
    }
    SettingsScreenBody(
        onBackClick = result.navBack,
        showLoading = result.loading,
        changeTheme = result.changeTheme,
        encryption = result.encryption,
        changeEncryption = result.changeEncryption,
        changePassword = result.changePassword,
        showCipherVersion = result.showCipherVersion,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun SettingsScreenBody(
    onBackClick: () -> Unit = {},
    showLoading: Boolean = true,
    changeTheme: () -> Unit = {},
    encryption: Boolean = false,
    changeEncryption: (Boolean) -> Unit = {},
    changePassword: () -> Unit = {},
    showCipherVersion: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.settings)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
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
            if (showLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            PreferenceCategory(stringResource(Res.string.theme), Icons.Default.Brightness4)
            ThemePreferenceItem(onClick = changeTheme)
            PreferenceCategory(stringResource(Res.string.security), Icons.Default.Security)
            Preference(
                modifier = Modifier.semantics {
                    contentDescription = enableEncryptionPrefTitle
                    toggleableState = ToggleableState(encryption)
                    testTag = enableEncryptionPrefTitle
                },
                title = enableEncryptionPrefTitle,
                vector = Icons.Default.Lock,
                onClick = { changeEncryption(!encryption) }
            ) {
                Switch(checked = encryption, onCheckedChange = changeEncryption)
            }
            Preference(
                title = stringResource(Res.string.pref_title_set_password),
                vector = Icons.Default.Password,
                onClick = changePassword
            )
            Preference(
                title = stringResource(Res.string.pref_title_check_cipher_version),
                vector = Icons.Filled.FileLock,
                onClick = showCipherVersion
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
