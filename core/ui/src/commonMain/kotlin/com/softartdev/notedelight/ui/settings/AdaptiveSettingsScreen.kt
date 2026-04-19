@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.ui.settings

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.di.PreviewKoin
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.presentation.settings.SettingsCategoriesViewModel
import com.softartdev.notedelight.presentation.settings.SettingsViewModel
import com.softartdev.notedelight.ui.VerticalPaneExpansionDragHandle
import com.softartdev.notedelight.ui.settings.detail.SettingsDetailScreen
import com.softartdev.notedelight.ui.settings.master.SettingsMasterScreen
import com.softartdev.theme.material3.PreferableMaterialTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdaptiveSettingsScreen(
    router: Router = koinInject(),
    categoriesViewModel: SettingsCategoriesViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
) {
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
        listPane = {
            SettingsMasterScreen(categoriesViewModel = categoriesViewModel)
        },
        detailPane = {
            SettingsDetailScreen(settingsViewModel = settingsViewModel)
        },
        paneExpansionDragHandle = { paneExpansionState: PaneExpansionState ->
            VerticalPaneExpansionDragHandle(paneExpansionState)
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
