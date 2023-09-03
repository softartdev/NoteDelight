@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult
import com.softartdev.theme.pref.PreferableMaterialTheme.themePrefs
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onItemClicked: (id: Long) -> Unit,
    onSettingsClick: () -> Unit,
    navSignIn: () -> Unit
) {
    val noteListState: State<NoteListResult> = mainViewModel.resultStateFlow.collectAsState()
    DisposableEffect(mainViewModel) {
        mainViewModel.updateNotes()
        onDispose(mainViewModel::onCleared)
    }
    MainScreen(noteListState, onItemClicked, onSettingsClick, navSignIn)
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    onItemClicked: (id: Long) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    navSignIn: () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(MR.strings.app_name)) },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(MR.strings.settings))
                }
            })
    }, content = {
        Box(modifier = Modifier.padding(it)) {
            when (val noteListResult = noteListState.value) {
                is NoteListResult.Loading -> Loader()
                is NoteListResult.Success -> {
                    val notes: List<Note> = noteListResult.result
                    if (notes.isNotEmpty()) NoteList(notes, onItemClicked) else Empty()
                }
                is NoteListResult.NavSignIn -> navSignIn()
                is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
            }
            themePrefs.showDialogIfNeed()
        }
    }, floatingActionButton = {
        val text = stringResource(MR.strings.create_note)
        ExtendedFloatingActionButton(
            text = { Text(text) },
            onClick = { onItemClicked(0) },
            icon = { Icon(Icons.Default.Add, contentDescription = Icons.Default.Add.name) },
            modifier = Modifier.clearAndSetSemantics { contentDescription = text }
        )
    })

@Preview
@Composable
fun PreviewMainScreen() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(testNotes))
    }
    MainScreen(noteListState)
}