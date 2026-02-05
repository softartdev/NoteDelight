@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.settings.master

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.presentation.settings.SettingsCategoriesAction
import com.softartdev.notedelight.presentation.settings.SettingsCategoriesResult
import com.softartdev.notedelight.presentation.settings.SettingsCategoriesViewModel
import com.softartdev.notedelight.model.SettingsCategory
import com.softartdev.notedelight.ui.BackHandler
import com.softartdev.notedelight.ui.selectedListItemColor
import com.softartdev.notedelight.util.icon
import com.softartdev.notedelight.util.tag
import com.softartdev.notedelight.util.titleRes
import com.softartdev.theme.material3.PreferableMaterialTheme
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsMasterScreen(categoriesViewModel: SettingsCategoriesViewModel) {
    LaunchedEffect(categoriesViewModel) {
        categoriesViewModel.launchCategories()
    }
    val resultState: State<SettingsCategoriesResult> = categoriesViewModel.stateFlow.collectAsState()
    val result: SettingsCategoriesResult = resultState.value
    val refreshState: State<Boolean> = remember {
        derivedStateOf { resultState.value.loading }
    }
    SettingsMasterScreenBody(
        selectedCategoryId = result.selectedCategoryId,
        onCategoryClick = { category: SettingsCategory ->
            categoriesViewModel.onAction(SettingsCategoriesAction.SelectCategory(category))
        },
        onRefresh = { categoriesViewModel.onAction(SettingsCategoriesAction.Refresh) },
        refreshState = refreshState,
        onNavigateBack = { categoriesViewModel.onAction(SettingsCategoriesAction.NavBack) }
    )
    BackHandler(enabled = result.selectedCategoryId == null) {
        categoriesViewModel.onAction(SettingsCategoriesAction.NavBack)
    }
}

@Composable
fun SettingsMasterScreenBody(
    selectedCategoryId: Long? = null,
    onCategoryClick: (SettingsCategory) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onRefresh: () -> Unit = {},
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    refreshState: State<Boolean> = remember { derivedStateOf { false } },
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.settings)) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
        )
    },
    content = { paddingValues: PaddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = refreshState.value,
            onRefresh = onRefresh,
            state = pullToRefreshState
        ) {
            LaunchedEffect(key1 = refreshState.value) {
                pullToRefreshState.animateToHidden()
            }
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                SettingsCategory.entries.forEach { category: SettingsCategory ->
                    SettingsCategoryItem(
                        category = category,
                        selected = selectedCategoryId == category.id,
                        modifier = Modifier.testTag(category.tag),
                        onClick = { onCategoryClick(category) },
                    )
                }
            }
        }
    }
)

@Composable
private fun SettingsCategoryItem(
    category: SettingsCategory,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) = ListItem(
    modifier = modifier.clickable(onClick = onClick),
    leadingContent = { Icon(imageVector = category.icon, contentDescription = stringResource(category.titleRes)) },
    headlineContent = { Text(text = stringResource(category.titleRes)) },
    colors = ListItemDefaults.colors(containerColor = selectedListItemColor(selected)),
)

@Preview
@Composable
fun PreviewSettingsMasterScreen() = PreferableMaterialTheme {
    SettingsMasterScreenBody(
        selectedCategoryId = SettingsCategory.Security.id,
    )
}
