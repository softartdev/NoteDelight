@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.createMultiplatformMessage
import com.softartdev.notedelight.shared.presentation.settings.SecurityResult
import com.softartdev.notedelight.shared.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.ui.dialog.showChangePassword
import com.softartdev.notedelight.ui.dialog.showConfirmPassword
import com.softartdev.notedelight.ui.dialog.showEnterPassword
import com.softartdev.notedelight.ui.dialog.showError
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.theme.material3.ThemePreferenceItem
import com.softartdev.theme.pref.DialogHolder
import com.softartdev.theme.pref.PreferableMaterialTheme.themePrefs
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val securityResultState: State<SecurityResult> = settingsViewModel.resultStateFlow.collectAsState()
    DisposableEffect(settingsViewModel) {
        settingsViewModel.checkEncryption()
        onDispose(settingsViewModel::onCleared)
    }
    val encryptionState = remember { mutableStateOf(false) }
    val dialogHolder: DialogHolder = themePrefs.dialogHolder
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    when (val securityResult = securityResultState.value) {
        is SecurityResult.Loading -> Unit
        is SecurityResult.EncryptEnable -> {
            encryptionState.value = securityResult.encryption
        }

        is SecurityResult.PasswordDialog -> dialogHolder.showEnterPassword(doAfterDismiss = settingsViewModel::checkEncryption)
        is SecurityResult.SetPasswordDialog -> dialogHolder.showConfirmPassword(doAfterDismiss = settingsViewModel::checkEncryption)
        is SecurityResult.ChangePasswordDialog -> dialogHolder.showChangePassword(doAfterDismiss = settingsViewModel::checkEncryption)
        is SecurityResult.SnackBar -> coroutineScope.launch {
            snackbarHostState.showSnackbar(message = securityResult.message.toString())
        }
        is SecurityResult.Error -> dialogHolder.showError(securityResult.message)
    }
    SettingsScreenBody(
        onBackClick = onBackClick,
        showLoading = securityResultState.value is SecurityResult.Loading,
        encryptionState = encryptionState,
        changeEncryption = settingsViewModel::changeEncryption,
        changePassword = settingsViewModel::changePassword,
        showCipherVersion = settingsViewModel::showCipherVersion,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun SettingsScreenBody(
    onBackClick: () -> Unit = {},
    showLoading: Boolean = true,
    encryptionState: MutableState<Boolean> = mutableStateOf(false),
    changeEncryption: (Boolean) -> Unit = {},
    changePassword: () -> Unit = {},
    showCipherVersion: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(MR.strings.settings)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = Icons.Default.ArrowBack.name
                    )
                }
            },
        )
    },
    content = {
        Box(modifier = Modifier.padding(it)) {
            Column {
                if (showLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
                PreferenceCategory(stringResource(MR.strings.theme), Icons.Default.Brightness4)
                ThemePreferenceItem()
                PreferenceCategory(stringResource(MR.strings.security), Icons.Default.Security)
                Preference(
                    modifier = Modifier.semantics {
                        contentDescription = MR.strings.pref_title_enable_encryption.contextLocalized()
                        toggleableState = ToggleableState(encryptionState.value)
                        testTag = MR.strings.pref_title_enable_encryption.contextLocalized()
                    },
                    title = stringResource(MR.strings.pref_title_enable_encryption),
                    vector = Icons.Default.Lock,
                    onClick = { changeEncryption(!encryptionState.value) }
                ) {
                    Switch(checked = encryptionState.value, onCheckedChange = changeEncryption)
                }
                Preference(
                    title = stringResource(MR.strings.pref_title_set_password),
                    vector = Icons.Default.Password,
                    onClick = changePassword
                )
                Preference(
                    title = stringResource(MR.strings.pref_title_check_cipher_version),
                    vector = Icons.Filled.FileLock,
                    onClick = showCipherVersion
                )
                Spacer(Modifier.height(32.dp))
                ListItem(
                    headlineContent = {},
                    supportingContent = { Text(createMultiplatformMessage()) }
                )
            }
            themePrefs.showDialogIfNeed()
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
