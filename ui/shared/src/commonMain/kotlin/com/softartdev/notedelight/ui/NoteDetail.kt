@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.note.NoteAction
import com.softartdev.notedelight.presentation.note.NoteResult
import com.softartdev.notedelight.presentation.note.NoteViewModel
import com.softartdev.theme.material3.PreferableMaterialTheme
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.action_delete_note
import notedelight.ui.shared.generated.resources.action_edit_title
import notedelight.ui.shared.generated.resources.action_save_note
import notedelight.ui.shared.generated.resources.type_text
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoteDetail(noteViewModel: NoteViewModel) {
    LaunchedEffect(noteViewModel) {
        noteViewModel.launchCollectingSelectedNoteId()
    }
    val result: NoteResult by noteViewModel.stateFlow.collectAsState()
    when (result.note) {
        null -> DetailPanePlaceholder()
        else -> NoteDetail(noteViewModel, result)
    }
}

@Composable
fun NoteDetail(
    noteViewModel: NoteViewModel,
    result: NoteResult
) {
    val titleState: MutableState<String> = remember(key1 = noteViewModel, key2 = result) {
        mutableStateOf(result.note?.title ?: "")
    }
    val textState: MutableState<String> = remember(key1 = noteViewModel, key2 = result) {
        mutableStateOf(result.note?.text ?: "")
    }
    NoteDetailBody(
        result = result,
        titleState = titleState,
        textState = textState,
        onAction = noteViewModel::onAction
    )
    BackHandler { noteViewModel.onAction(NoteAction.CheckSaveChange(titleState.value, textState.value)) }
}

@Composable
fun NoteDetailBody(
    result: NoteResult = NoteResult(),
    titleState: MutableState<String> = mutableStateOf("Title"),
    textState: MutableState<String> = mutableStateOf("Text"),
    onAction: (action: NoteAction) -> Unit = {},
) = Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
        TopAppBar(
            title = { Text(text = titleState.value, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = {
                    onAction(NoteAction.CheckSaveChange(titleState.value, textState.value))
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
            actions = {
                IconButton(onClick = { onAction(NoteAction.Save(titleState.value, textState.value)) }) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(Res.string.action_save_note)
                    )
                }
                IconButton(onClick = { onAction(NoteAction.Edit) }) {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = stringResource(Res.string.action_edit_title)
                    )
                }
                IconButton(onClick = { onAction(NoteAction.Delete) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.action_delete_note)
                    )
                }
            }
        )
    },
    content = { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val scrollState = rememberScrollState()
            val scrollProgressState: State<Float> = remember {
                derivedStateOf {
                    val progress = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                    return@derivedStateOf if (progress.isNaN()) 0f else progress
                }
            }
            when {
                result.loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                scrollProgressState.value > 0f -> LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = scrollProgressState::value,
                    drawStopIndicator = {}
                )
            }
            TextField(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(state = scrollState),
                value = textState.value,
                onValueChange = textState::value::set,
                label = { Text(stringResource(Res.string.type_text)) },
            )
        }
    },
)

@Preview
@Composable
fun PreviewNoteDetailBody() = PreferableMaterialTheme { NoteDetailBody() }