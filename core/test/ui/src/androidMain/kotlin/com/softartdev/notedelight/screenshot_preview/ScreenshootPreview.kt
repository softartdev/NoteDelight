package com.softartdev.notedelight.screenshot_preview

import android.content.res.Configuration
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.softartdev.notedelight.di.PreviewKoin
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.SettingsCategory
import com.softartdev.notedelight.presentation.main.NoteListResult
import com.softartdev.notedelight.presentation.note.NoteResult
import com.softartdev.notedelight.presentation.settings.SettingsCategoriesResult
import com.softartdev.notedelight.presentation.settings.SettingsResult
import com.softartdev.notedelight.presentation.signin.SignInResult
import com.softartdev.notedelight.ui.main.AdaptiveMainScreen
import com.softartdev.notedelight.ui.main.NoteDetailBody
import com.softartdev.notedelight.ui.settings.AdaptiveSettingsScreen
import com.softartdev.notedelight.ui.signin.SignInScreenBody
import com.softartdev.theme.material3.PreferableMaterialTheme

private const val STORE_DEVICE_PHONE = "spec:width=1080px,height=1920px,dpi=420"
private const val STORE_DEVICE_TABLET = "spec:width=1920px,height=1200px,dpi=240"

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StorePhoneLightNotesPreview(
    @PreviewParameter(ScreenshotPreviewProvider::class) noteListResult: NoteListResult.Success
) = StoreScreenshotTheme { StoreNotesScreen(noteListResult, false) }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StorePhoneDarkNotesPreview(
    @PreviewParameter(ScreenshotPreviewProvider::class) noteListResult: NoteListResult.Success
) = StoreScreenshotTheme { StoreNotesScreen(noteListResult, false) }

@Preview(device = STORE_DEVICE_TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StoreTabletLightNotesPreview(
    @PreviewParameter(ScreenshotPreviewProvider::class) noteListResult: NoteListResult.Success
) = StoreScreenshotTheme { StoreNotesScreen(noteListResult) }

@Preview(device = STORE_DEVICE_TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StoreTabletDarkNotesPreview(
    @PreviewParameter(ScreenshotPreviewProvider::class) noteListResult: NoteListResult.Success
) = StoreScreenshotTheme { StoreNotesScreen(noteListResult) }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StorePhoneLightNoteDetailPreview() = StoreScreenshotTheme { StoreNoteDetailScreen() }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StorePhoneDarkNoteDetailPreview() = StoreScreenshotTheme { StoreNoteDetailScreen() }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StorePhoneLightSignInPreview() = StoreScreenshotTheme { StoreSignInScreen() }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StorePhoneDarkSignInPreview() = StoreScreenshotTheme { StoreSignInScreen() }

@Preview(device = STORE_DEVICE_TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StoreTabletLightSignInPreview() = StoreScreenshotTheme { StoreSignInScreen() }

@Preview(device = STORE_DEVICE_TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StoreTabletDarkSignInPreview() = StoreScreenshotTheme { StoreSignInScreen() }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StorePhoneLightSecuritySettingsPreview() = StoreScreenshotTheme { StoreSettingsScreen(null) }

@Preview(device = STORE_DEVICE_PHONE, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StorePhoneDarkSecuritySettingsPreview() = StoreScreenshotTheme { StoreSettingsScreen(null) }

@Preview(device = STORE_DEVICE_TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StoreTabletLightSecuritySettingsPreview() = StoreScreenshotTheme { StoreSettingsScreen() }

@Preview(device = STORE_DEVICE_TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StoreTabletDarkSecuritySettingsPreview() = StoreScreenshotTheme { StoreSettingsScreen() }

@Composable
private fun StoreScreenshotTheme(content: @Composable () -> Unit) = PreviewKoin {
    PreferableMaterialTheme { content() }
}

@Composable
private fun StoreNotesScreen(
    noteListResult: NoteListResult.Success,
    showSelectedNote: Boolean = true
) {
    val selectedNote: Note? = if (showSelectedNote) ScreenshotPreviewProvider.selected else null
    AdaptiveMainScreen(
        noteListResultState = remember {
            mutableStateOf(noteListResult.copy(selectedId = selectedNote?.id))
        },
        noteDetailState = remember { mutableStateOf(NoteResult(note = selectedNote)) },
        onMainAction = {},
        onNoteAction = {},
    )
}

@Composable
private fun StoreNoteDetailScreen(note: Note = ScreenshotPreviewProvider.selected) {
    val result: NoteResult = remember { NoteResult(note = note) }
    val textState: TextFieldState = remember(note.id) {
        TextFieldState(
            initialText = note.text,
            initialSelection = TextRange(note.text.length),
        )
    }
    NoteDetailBody(
        result = result,
        textState = textState,
    )
}

@Composable
private fun StoreSignInScreen() {
    val result: SignInResult = remember { SignInResult(biometricVisible = true) }
    val passwordState: MutableState<String> = remember {
        mutableStateOf("secure demo password")
    }
    SignInScreenBody(
        showLoading = result.loading,
        passwordState = passwordState,
        biometricVisible = result.biometricVisible,
    )
}

@Composable
private fun StoreSettingsScreen(category: SettingsCategory? = SettingsCategory.Security) {
    val categoriesResult: SettingsCategoriesResult = remember {
        SettingsCategoriesResult(selectedCategoryId = category?.id)
    }
    val settingsResult: SettingsResult = remember(category) {
        SettingsResult(
            encryption = true,
            biometricEnabled = true,
            biometricAvailable = true,
            fileListVisible = true,
            appVersion = "8.5.5",
            selectedCategory = category,
        )
    }
    AdaptiveSettingsScreen(
        settingsCategoriesResultState = remember { mutableStateOf(categoriesResult) },
        onSettingsCategoriesAction = {},
        settingsResultState = remember { mutableStateOf(settingsResult) },
        onSettingsAction = {},
    )
}
