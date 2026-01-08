@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.main

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.presentation.main.MainAction
import com.softartdev.notedelight.presentation.main.MainViewModel
import com.softartdev.notedelight.presentation.main.NoteListResult
import com.softartdev.notedelight.ui.Empty
import com.softartdev.notedelight.ui.Error
import com.softartdev.notedelight.ui.Loader
import com.softartdev.notedelight.util.CREATE_NOTE_FAB_TAG
import com.softartdev.notedelight.util.MAIN_SETTINGS_BUTTON_TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.app_name
import notedelight.ui.shared.generated.resources.create_note
import notedelight.ui.shared.generated.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    LaunchedEffect(mainViewModel) {
        mainViewModel.launchNotes()
    }
    val noteListState: State<NoteListResult> = mainViewModel.stateFlow.collectAsState()
    MainScreen(
        noteListState = noteListState,
        onAction = mainViewModel::onAction
    )
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    onAction: (action: MainAction) -> Unit = {},
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    refreshState: State<Boolean> = remember { derivedStateOf { noteListState.value is NoteListResult.Loading } }
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.app_name)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(MAIN_SETTINGS_BUTTON_TAG),
                    onClick = { onAction(MainAction.OnSettingsClick) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.settings)
                    )
                }
            }
        )
    }, content = { paddingValues: PaddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = refreshState.value,
            onRefresh = { onAction(MainAction.OnRefresh) },
            state = pullToRefreshState
        ) {
            LaunchedEffect(key1 = noteListState.value) {
                pullToRefreshState.animateToHidden()
            }
            when (val noteListResult = noteListState.value) {
                is NoteListResult.Loading -> Loader(modifier = Modifier.align(Alignment.Center))
                is NoteListResult.Success -> {
                    val pagingItems: LazyPagingItems<Note> = noteListResult.result.collectAsLazyPagingItems()
                    when {
                        pagingItems.itemCount > 0 -> NoteList(
                            pagingItems = pagingItems,
                            onItemClicked = { id -> onAction(MainAction.OnNoteClick(id)) },
                            selectedNoteId = noteListResult.selectedId
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
            modifier = Modifier.testTag(CREATE_NOTE_FAB_TAG),
            text = { Text(text) },
            onClick = { onAction(MainAction.OnNoteClick(0)) },
            icon = { Icon(Icons.Default.Add, contentDescription = Icons.Default.Add.name) },
        )
    }
)

@Preview
@Composable
fun PreviewMainScreen() {
    val pagingData: PagingData<Note> = PagingData.from(data = TestSchema.notes)
    val pagingFlow: Flow<PagingData<Note>> = flowOf(pagingData)
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(pagingFlow, null))
    }
    MainScreen(noteListState)
}
