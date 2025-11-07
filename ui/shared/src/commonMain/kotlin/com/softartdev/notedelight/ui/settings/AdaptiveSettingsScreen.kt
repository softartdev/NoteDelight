@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.di.PreviewKoin
import com.softartdev.notedelight.ui.SettingsDetailPanePlaceholder
import com.softartdev.theme.material3.PreferableMaterialTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdaptiveSettingsScreen() {
    val navigator: ThreePaneScaffoldNavigator<Long> = rememberListDetailPaneScaffoldNavigator<Long>()
    val paneExpansionState: PaneExpansionState = rememberPaneExpansionState()
    val mutableInteractionSource = remember { MutableInteractionSource() }
    ListDetailPaneScaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = { SettingsScreen(settingsViewModel = koinViewModel()) },
        detailPane = { SettingsDetailPanePlaceholder() },
        paneExpansionDragHandle = {
            VerticalDragHandle(
                modifier = Modifier.paneExpansionDraggable(
                    state = paneExpansionState,
                    minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                    interactionSource = mutableInteractionSource,
                ),
                interactionSource = mutableInteractionSource
            )
        },
        paneExpansionState = paneExpansionState
    )
}

@Preview
@Composable
fun PreviewAdaptiveSettingsScreen() = PreviewKoin {
    PreferableMaterialTheme {
        AdaptiveSettingsScreen()
    }
}