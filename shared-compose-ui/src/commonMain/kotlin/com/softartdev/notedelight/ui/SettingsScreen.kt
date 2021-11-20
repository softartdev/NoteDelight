@file:OptIn(ExperimentalMaterialApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.createMultiplatformMessage
import com.softartdev.notedelight.shared.presentation.settings.SecurityResult
import com.softartdev.notedelight.shared.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.ui.dialog.DialogHolder

@Composable
fun SettingsScreen(onBackClick: () -> Unit, appModule: AppModule, darkThemeState: MutableState<Boolean>) {
    val settingsViewModel: SettingsViewModel = remember(appModule::settingsViewModel)
    val securityResultState: State<SecurityResult> = settingsViewModel.resultStateFlow.collectAsState()
    DisposableEffect(settingsViewModel) {
        settingsViewModel.checkEncryption()
        onDispose(settingsViewModel::onCleared)
    }
    val encryptionState = remember { mutableStateOf(false) }
    val dialogHolder: DialogHolder = remember { DialogHolder() }
    when (val securityResult = securityResultState.value) {
        is SecurityResult.Loading -> Unit
        is SecurityResult.EncryptEnable -> {
            encryptionState.value = securityResult.encryption
        }
        is SecurityResult.PasswordDialog -> dialogHolder.showEnterPassword(appModule)
        is SecurityResult.SetPasswordDialog -> dialogHolder.showConfirmPassword(appModule)
        is SecurityResult.ChangePasswordDialog -> dialogHolder.showChangePassword(appModule)
        is SecurityResult.Error -> dialogHolder.showError(securityResult.message)
    }
    SettingsScreenBody(
        onBackClick = onBackClick,
        showLoading = securityResultState.value is SecurityResult.Loading,
        darkThemeState = darkThemeState,
        encryptionState = encryptionState,
        changeEncryption = settingsViewModel::changeEncryption,
        changePassword = settingsViewModel::changePassword,
        showDialogIfNeed = dialogHolder.showDialogIfNeed
    )
}

@Composable
fun SettingsScreenBody(
    onBackClick: () -> Unit = {},
    showLoading: Boolean = true,
    darkThemeState: MutableState<Boolean> = mutableStateOf(isSystemInDarkTheme()),
    encryptionState: MutableState<Boolean> = mutableStateOf(false),
    changeEncryption: (Boolean) -> Unit = {},
    changePassword: () -> Unit = {},
    showDialogIfNeed: @Composable () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(MR.strings.settings.localized()) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
        )
    }
) {
    Box {
        Column {
            if (showLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            PreferenceCategory(MR.strings.theme.localized(), Icons.Default.Brightness4)
            Preference(
                title = MR.strings.choose_theme.localized(),
                vector = Icons.Default.SettingsBrightness,
                secondaryText = { Text(MR.strings.system_default.localized()) },//TODO show current
                trailing = { // TODO change by dialog
                    Switch(checked = darkThemeState.value, onCheckedChange = { darkThemeState.value = it })
                }
            )
            PreferenceCategory(MR.strings.security.localized(), Icons.Default.Security)
            Preference(
                title = MR.strings.pref_title_enable_encryption.localized(),
                vector = Icons.Default.Lock,
                trailing = {
                    Switch(checked = encryptionState.value, onCheckedChange = changeEncryption)
                }
            )
            Preference(MR.strings.pref_title_set_password.localized(), Icons.Default.Password, changePassword)
            Spacer(Modifier.height(32.dp))
            ListItem(text = {}, icon = {}, secondaryText = { Text(createMultiplatformMessage()) })
        }
        showDialogIfNeed()
    }
}

@Composable
fun PreferenceCategory(title: String, vector: ImageVector) = ListItem(
    icon = { Icon(imageVector = vector, contentDescription = title) },
    text = {
        Text(text = title,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondaryVariant)
    }
)

@Composable
fun Preference(
    title: String,
    vector: ImageVector,
    onClick: () -> Unit = {},
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) = ListItem(
    modifier = Modifier.clickable(onClick = onClick),
    icon = { Icon(imageVector = vector, contentDescription = title) },
    text = { Text(text = title) },
    secondaryText = secondaryText,
    trailing = trailing
)

@Preview
@Composable
fun PreviewSettingsScreenBody() = SettingsScreenBody()
