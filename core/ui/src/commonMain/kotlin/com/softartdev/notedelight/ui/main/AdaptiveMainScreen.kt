@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.ui.main

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.di.PreviewKoin
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.main.MainAction
import com.softartdev.notedelight.presentation.main.MainViewModel
import com.softartdev.notedelight.presentation.main.NoteListResult
import com.softartdev.notedelight.presentation.note.NoteAction
import com.softartdev.notedelight.presentation.note.NoteResult
import com.softartdev.notedelight.presentation.note.NoteViewModel
import com.softartdev.notedelight.ui.NavBackHandler
import com.softartdev.notedelight.ui.VerticalPaneExpansionDragHandle
import com.softartdev.theme.material3.PreferableMaterialTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdaptiveMainScreen(
    router: Router = koinInject(),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    mainViewModel: MainViewModel = koinViewModel(),
    noteViewModel: NoteViewModel = koinViewModel(),
) {
    LaunchedEffect(mainViewModel, noteViewModel) {
        mainViewModel.launchNotes()
        noteViewModel.launchCollectingSelectedNoteId()
    }
    AdaptiveMainScreen(
        router = router,
        snackbarHostState = snackbarHostState,
        noteListResultState = mainViewModel.stateFlow.collectAsState(),
        onMainAction = mainViewModel::onAction,
        noteDetailState = noteViewModel.stateFlow.collectAsState(),
        onNoteAction = noteViewModel::onAction,
    )
}

@Composable
fun AdaptiveMainScreen(
    router: Router = koinInject(),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    noteListResultState: State<NoteListResult>,
    onMainAction: (action: MainAction) -> Unit,
    noteDetailState: State<NoteResult>,
    onNoteAction: (action: NoteAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val navigator: ThreePaneScaffoldNavigator<Long> = rememberListDetailPaneScaffoldNavigator<Long>()
    val paneExpansionState: PaneExpansionState = rememberPaneExpansionState()
    DisposableEffect(key1 = router, key2 = navigator) {
        router.setAdaptiveNavigator(navigator)
        onDispose { router.releaseAdaptiveNavigator(navigator) }
    }
    ListDetailPaneScaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = { MainScreen(noteListResultState, onMainAction, snackbarHostState = snackbarHostState) },
        detailPane = { NoteDetailScreen(noteDetailState, onNoteAction) },
        paneExpansionDragHandle = ThreePaneScaffoldScope::VerticalPaneExpansionDragHandle,
        paneExpansionState = paneExpansionState
    )
    NavBackHandler(navigator.canNavigateBack()) { coroutineScope.launch { navigator.navigateBack() } }
}

@Preview
@Composable
fun PreviewAdaptiveScreen() = PreviewKoin { PreferableMaterialTheme { AdaptiveMainScreen() } }
