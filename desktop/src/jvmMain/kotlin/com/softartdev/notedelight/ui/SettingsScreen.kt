package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import io.github.aakira.napier.Napier

@Composable
fun SettingsScreen(onBackClick: () -> Unit, appModule: AppModule, darkThemeState: MutableState<Boolean>) {
    val settingsViewModel: SettingsViewModel = remember(appModule::settingsViewModel)
    val securityResultState: State<SecurityResult> = settingsViewModel.resultStateFlow.collectAsState()
    DisposableEffect(settingsViewModel) {
        settingsViewModel.checkEncryption()
        onDispose(settingsViewModel::onCleared)
    }
    when (val securityResult = securityResultState.value) {
        is SecurityResult.Loading -> {
            Napier.d("loading: $securityResult")
            //TODO show
        }
        is SecurityResult.EncryptEnable -> {
            Napier.d("encrypt enable: ${securityResult.encryption}")
            //TODO show
        }
        is SecurityResult.PasswordDialog -> TODO()
        is SecurityResult.SetPasswordDialog -> TODO()
        is SecurityResult.ChangePasswordDialog -> TODO()
        is SecurityResult.Error -> {
            Napier.e("error: ${securityResult.message}")
            //TODO show
        }
    }
    SettingsScreenBody(onBackClick, darkThemeState)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreenBody(
    onBackClick: () -> Unit = {},
    darkThemeState: MutableState<Boolean> = mutableStateOf(isSystemInDarkTheme()),
    encryptionState: MutableState<Boolean> = mutableStateOf(false),
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
    Column {
        PreferenceCategory(MR.strings.theme.localized(), Icons.Default.Brightness4)
        Preference(
            title = MR.strings.choose_theme.localized(),
            vector = Icons.Default.SettingsBrightness,
            secondaryText = { Text(MR.strings.system_default.localized()) },//TODO show current
            trailing = {
                Switch(checked = darkThemeState.value, onCheckedChange = { darkThemeState.value = it })
            }
        )
        PreferenceCategory(MR.strings.security.localized(), Icons.Default.Security)
        Preference(
            title = MR.strings.pref_title_enable_encryption.localized(),
            vector = Icons.Default.Lock,
            trailing = {
                Switch(checked = encryptionState.value, onCheckedChange = { encryptionState.value = it })
            }
        )
        Preference(MR.strings.pref_title_set_password.localized(), Icons.Default.Password)
        Spacer(Modifier.height(32.dp))
        ListItem(text = {}, icon = {}, secondaryText = { Text(createMultiplatformMessage()) })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PreferenceCategory(title: String, vector: ImageVector) = ListItem(
    icon = { Icon(imageVector = vector, contentDescription = title) },
    text = {
        Text(text = title,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondaryVariant)
    }
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Preference(
    title: String,
    vector: ImageVector,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) = ListItem(
    icon = { Icon(imageVector = vector, contentDescription = title) },
    text = { Text(text = title) },
    secondaryText = secondaryText,
    trailing = trailing
)

@Preview
@Composable
fun PreviewSettingsScreenBody() = SettingsScreenBody()
