@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.defaultDragHandleSemantics
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.di.PreviewKoin
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.navigation.RouterImpl
import com.softartdev.notedelight.ui.BackHandler
import com.softartdev.notedelight.ui.MainScreen
import com.softartdev.notedelight.ui.NoteDetail
import com.softartdev.theme.material3.PreferableMaterialTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdaptiveScreen(router: Router = RouterImpl()) {
    val coroutineScope = rememberCoroutineScope()
    val navigator: ThreePaneScaffoldNavigator<Long> = rememberListDetailPaneScaffoldNavigator<Long>()
    val paneExpansionState: PaneExpansionState = rememberPaneExpansionState()
    val mutableInteractionSource = remember { MutableInteractionSource() }
    DisposableEffect(key1 = router, key2 = navigator) {
        router.setAdaptiveNavigator(navigator)
        onDispose(router::releaseAdaptiveNavigator)
    }
    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch { navigator.navigateBack() }
    }
    ListDetailPaneScaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            MainScreen(mainViewModel = koinViewModel())
        },
        detailPane = {
            NoteDetail(noteViewModel = koinViewModel())
        },
        paneExpansionDragHandle = {
            VerticalDragHandle(
                modifier = Modifier.paneExpansionDraggable(
                    state = paneExpansionState,
                    minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                    interactionSource = mutableInteractionSource,
                    semanticsProperties = paneExpansionState.defaultDragHandleSemantics(),
                ),
                interactionSource = mutableInteractionSource
            )
        },
        paneExpansionState = paneExpansionState
    )
}

@Preview
@Composable
fun PreviewAdaptiveScreen() = PreviewKoin {
    PreferableMaterialTheme {
        AdaptiveScreen()
    }
}
