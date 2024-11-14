@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.TestSchema
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.app_name
import notedelight.shared_compose_ui.generated.resources.create_note
import notedelight.shared_compose_ui.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(mainViewModel) {
        mainViewModel.launchNotes()
    }
    val noteListState: State<NoteListResult> = mainViewModel.stateFlow.collectAsState()
    MainScreen(
        noteListState = noteListState,
        onItemClicked = mainViewModel::onNoteClicked,
        onSettingsClick = mainViewModel::onSettingsClicked,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    onItemClicked: (id: Long) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.app_name)) },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.settings)
                    )
                }
            })
    }, content = { paddingValues: PaddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val noteListResult = noteListState.value) {
                is NoteListResult.Loading -> Loader(modifier = Modifier.align(Alignment.Center))
                is NoteListResult.Success -> {
                    val pagingItems: LazyPagingItems<Note> = noteListResult.result.collectAsLazyPagingItems()
                    when {
                        pagingItems.itemCount > 0 -> NoteList(
                            pagingItems = pagingItems,
                            onItemClicked = onItemClicked,
                        )
                        else -> Empty()
                    }
                }
                is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
            }
        }
    }, floatingActionButton = {
        val text = stringResource(Res.string.create_note)
        ExtendedFloatingActionButton(
            text = { Text(text) },
            onClick = { onItemClicked(0) },
            icon = { Icon(Icons.Default.Add, contentDescription = Icons.Default.Add.name) },
            modifier = Modifier.clearAndSetSemantics { contentDescription = text }
        )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
)

@Preview
@Composable
fun PreviewMainScreen() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val pagingData: PagingData<Note> = PagingData.from(testNotes)
    val pagingFlow: Flow<PagingData<Note>> = flowOf(pagingData)
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(pagingFlow))
    }
    MainScreen(noteListState)
}
