@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.settings.detail

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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.softartdev.notedelight.model.SettingsCategory
import com.softartdev.notedelight.presentation.settings.SecurityResult
import com.softartdev.notedelight.presentation.settings.SettingsAction
import com.softartdev.notedelight.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.ui.BackHandler
import com.softartdev.notedelight.ui.SettingsDetailPanePlaceholder
import com.softartdev.notedelight.ui.icon.FileLock
import com.softartdev.notedelight.util.ENABLE_ENCRYPTION_SWITCH_TAG
import com.softartdev.notedelight.util.EXPORT_DATABASE_BUTTON_TAG
import com.softartdev.notedelight.util.IMPORT_DATABASE_BUTTON_TAG
import com.softartdev.notedelight.util.LANGUAGE_BUTTON_TAG
import com.softartdev.notedelight.util.SET_PASSWORD_BUTTON_TAG
import com.softartdev.notedelight.util.createMultiplatformMessage
import com.softartdev.notedelight.util.stringResource
import com.softartdev.notedelight.util.titleRes
import com.softartdev.theme.material3.PreferableMaterialTheme
import com.softartdev.theme.material3.ThemePreferenceItem
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.language
import notedelight.ui.shared.generated.resources.pref_subtitle_open_github
import notedelight.ui.shared.generated.resources.pref_title_check_cipher_version
import notedelight.ui.shared.generated.resources.pref_title_enable_encryption
import notedelight.ui.shared.generated.resources.pref_title_export_db
import notedelight.ui.shared.generated.resources.pref_title_file_list
import notedelight.ui.shared.generated.resources.pref_title_import_db
import notedelight.ui.shared.generated.resources.pref_title_set_password
import notedelight.ui.shared.generated.resources.pref_title_show_db_path
import notedelight.ui.shared.generated.resources.pref_title_source_code
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.testTag as semanticsTestTag

@Composable
fun SettingsDetailScreen(settingsViewModel: SettingsViewModel) {
    LaunchedEffect(settingsViewModel) {
        settingsViewModel.launchCollectingSelectedCategoryId()
    }
    val resultState: State<SecurityResult> = settingsViewModel.stateFlow.collectAsState()
    val result: SecurityResult = resultState.value
    val refreshState: State<Boolean> = remember {
        derivedStateOf { resultState.value.loading }
    }
    LifecycleResumeEffect(key1 = settingsViewModel) {
        settingsViewModel.updateSwitches()
        onPauseOrDispose {}
    }
    when (result.selectedCategory) {
        null -> SettingsDetailPanePlaceholder()
        else -> {
            SettingsDetailScreenBody(
                result = result,
                onBackClick = { settingsViewModel.onAction(SettingsAction.NavBack) },
                onAction = settingsViewModel::onAction,
                onRefresh = { settingsViewModel.onAction(SettingsAction.Refresh) },
                refreshState = refreshState,
            )
            BackHandler { settingsViewModel.onAction(SettingsAction.NavBack) }
        }
    }
}

@Composable
fun SettingsDetailScreenBody(
    result: SecurityResult = SecurityResult(),
    category: SettingsCategory = result.selectedCategory!!,
    onBackClick: () -> Unit = {},
    onAction: (action: SettingsAction) -> Unit = {},
    onRefresh: () -> Unit = {},
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    refreshState: State<Boolean> = remember { derivedStateOf { result.loading } },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(category.titleRes)) },
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
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = refreshState.value,
            onRefresh = onRefresh,
            state = pullToRefreshState
        ) {
            LaunchedEffect(key1 = refreshState.value) {
                pullToRefreshState.animateToHidden()
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (result.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                when (category) {
                    SettingsCategory.Appearance -> AppearancePreferences(result = result, onAction = onAction)
                    SettingsCategory.Security -> SecurityPreferences(result = result, onAction = onAction)
                    SettingsCategory.Backup -> BackupPreferences(onAction = onAction)
                    SettingsCategory.Info -> InfoPreferences(result = result, onAction = onAction)
                }
            }
        }
    },
)

@Composable
private fun AppearancePreferences(result: SecurityResult, onAction: (SettingsAction) -> Unit) {
    ThemePreferenceItem(onClick = { onAction(SettingsAction.ChangeTheme) })
    Preference(
        modifier = Modifier.testTag(LANGUAGE_BUTTON_TAG),
        title = stringResource(Res.string.language),
        vector = Icons.Default.Language,
        onClick = { onAction(SettingsAction.ChangeLanguage) },
        secondaryText = { Text(stringResource(result.language.stringResource)) }
    )
}

@Composable
private fun SecurityPreferences(result: SecurityResult, onAction: (SettingsAction) -> Unit) {
    val enableEncryptionPrefTitle = stringResource(Res.string.pref_title_enable_encryption)
    Preference(
        modifier = Modifier.semantics {
            contentDescription = enableEncryptionPrefTitle
            toggleableState = ToggleableState(result.encryption)
            semanticsTestTag = ENABLE_ENCRYPTION_SWITCH_TAG
        },
        title = enableEncryptionPrefTitle,
        vector = Icons.Default.Lock,
        onClick = { onAction(SettingsAction.ChangeEncryption(!result.encryption)) }
    ) {
        Switch(
            checked = result.encryption,
            onCheckedChange = { onAction(SettingsAction.ChangeEncryption(it)) }
        )
    }
    Preference(
        modifier = Modifier.testTag(SET_PASSWORD_BUTTON_TAG),
        title = stringResource(Res.string.pref_title_set_password),
        vector = Icons.Default.Password,
        onClick = { onAction(SettingsAction.ChangePassword) }
    )
    Preference(
        title = stringResource(Res.string.pref_title_check_cipher_version),
        vector = Icons.Filled.FileLock,
        onClick = { onAction(SettingsAction.ShowCipherVersion) }
    )
}

@Composable
private fun InfoPreferences(
    result: SecurityResult,
    onAction: (SettingsAction) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    Preference(
        title = stringResource(Res.string.pref_title_source_code),
        vector = Icons.Default.Code,
        onClick = { uriHandler.openUri("https://github.com/softartdev/NoteDelight") },
        secondaryText = { Text(stringResource(Res.string.pref_subtitle_open_github)) }
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

@Composable
private fun BackupPreferences(
    onAction: (SettingsAction) -> Unit,
    databaseFilePicker: DatabaseFilePicker = rememberDatabaseFilePicker(),
) {
    Preference(
        modifier = Modifier.testTag(EXPORT_DATABASE_BUTTON_TAG),
        title = stringResource(Res.string.pref_title_export_db),
        vector = Icons.Default.FileUpload,
        onClick = {
            databaseFilePicker.launchExport(SafeRepo.DB_NAME) { path: String? ->
                onAction(SettingsAction.ExportDatabase(path))
            }
        }
    )
    Preference(
        modifier = Modifier.testTag(IMPORT_DATABASE_BUTTON_TAG),
        title = stringResource(Res.string.pref_title_import_db),
        vector = Icons.Default.FileDownload,
        onClick = {
            databaseFilePicker.launchImport { path: String? ->
                onAction(SettingsAction.ImportDatabase(path))
            }
        }
    )
}

@Composable
private fun Preference(
    modifier: Modifier = Modifier,
    title: String,
    vector: ImageVector,
    onClick: () -> Unit = {},
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) = ListItem(
    modifier = modifier.clickable(onClick = onClick),
    leadingContent = { Icon(imageVector = vector, contentDescription = title) },
    headlineContent = { Text(text = title) },
    supportingContent = secondaryText,
    trailingContent = trailing
)

@Preview
@Composable
fun PreviewSettingsDetailScreenBody(
    @PreviewParameter(SettingsCategoryPreviewProvider::class) category: SettingsCategory
) = PreferableMaterialTheme {
    SettingsDetailScreenBody(
        result = SecurityResult(fileListVisible = true, selectedCategory = category),
    )
}
