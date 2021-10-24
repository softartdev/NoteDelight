package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
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

@Composable
fun SettingsScreenBody(
    onBackClick: () -> Unit = {},
    darkThemeState: MutableState<Boolean> = mutableStateOf(isSystemInDarkTheme()),
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
    Switch(checked = darkThemeState.value, onCheckedChange = { darkThemeState.value = it })
}

@Preview
@Composable
fun PreviewSettingsScreenBody() = SettingsScreenBody()
