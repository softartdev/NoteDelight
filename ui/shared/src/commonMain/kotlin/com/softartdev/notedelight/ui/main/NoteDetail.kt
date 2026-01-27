@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.note.NoteAction
import com.softartdev.notedelight.presentation.note.NoteResult
import com.softartdev.notedelight.presentation.note.NoteViewModel
import com.softartdev.notedelight.ui.BackHandler
import com.softartdev.notedelight.ui.MainDetailPanePlaceholder
import com.softartdev.notedelight.util.DELETE_NOTE_BUTTON_TAG
import com.softartdev.notedelight.util.EDIT_TITLE_BUTTON_TAG
import com.softartdev.notedelight.util.SAVE_NOTE_BUTTON_TAG
import com.softartdev.theme.material3.PreferableMaterialTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.action_delete_note
import notedelight.ui.shared.generated.resources.action_edit_title
import notedelight.ui.shared.generated.resources.action_save_note
import notedelight.ui.shared.generated.resources.type_text
import org.jetbrains.compose.resources.stringResource

const val NOTE_TEXT_FIELD_TAG: String = "NOTE_TEXT_FIELD_TAG"

@Composable
fun NoteDetail(noteViewModel: NoteViewModel) {
    LaunchedEffect(noteViewModel) {
        noteViewModel.launchCollectingSelectedNoteId()
    }
    val result: NoteResult by noteViewModel.stateFlow.collectAsState()
    when (result.note) {
        null -> MainDetailPanePlaceholder()
        else -> NoteDetail(
            result = result,
            onAction = noteViewModel::onAction,
            checkSaveChangeChannel = noteViewModel.checkSaveChangeChannel
        )
    }
}

@Composable
fun NoteDetail(
    result: NoteResult,
    onAction: (NoteAction) -> Unit,
    checkSaveChangeChannel: Channel<Unit>
) {
    // Change selected note on adaptive (tablet) layout must change the text too.
    // The `rememberTextFieldState` and `rememberSaveable` doesn't support `key` parameters.
    // The note id is used as a key to recreate the `TextFieldState` when the selected note changes.
    val textState: TextFieldState = remember(key1 = result.note?.id) {
        TextFieldState(
            initialText = result.note?.text ?: "",
            initialSelection = TextRange(result.note?.text?.length ?: 0)
        )
    }
    LaunchedEffect(checkSaveChangeChannel) {
        checkSaveChangeChannel.receiveAsFlow().collect {
            onAction(NoteAction.ShowCheckSaveChangeDialog(textState.text))
        }
    }
    NoteDetailBody(
        result = result,
        textState = textState,
        onAction = onAction
    )
    BackHandler { onAction(NoteAction.CheckSaveChange(textState.text)) }
}

@Composable
fun NoteDetailBody(
    result: NoteResult = NoteResult(),
    textState: TextFieldState = TextFieldState("Text"),
    onAction: (action: NoteAction) -> Unit = {},
) = Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
        TopAppBar(
            title = { Text(text = result.note?.title.orEmpty(), maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = { onAction(NoteAction.CheckSaveChange(textState.text)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(SAVE_NOTE_BUTTON_TAG),
                    onClick = { onAction(NoteAction.Save(textState.text)) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(Res.string.action_save_note)
                    )
                }
                IconButton(
                    modifier = Modifier.testTag(EDIT_TITLE_BUTTON_TAG),
                    onClick = { onAction(NoteAction.Edit) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = stringResource(Res.string.action_edit_title)
                    )
                }
                IconButton(
                    modifier = Modifier.testTag(DELETE_NOTE_BUTTON_TAG),
                    onClick = { onAction(NoteAction.Delete) },
                ) {
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
                    .testTag(tag = NOTE_TEXT_FIELD_TAG)
                    .weight(1F)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(state = scrollState),
                state = textState,
                label = { Text(stringResource(Res.string.type_text)) },
            )
        }
    },
)

@Preview
@Composable
fun PreviewNoteDetailBody() = PreferableMaterialTheme { NoteDetailBody() }
