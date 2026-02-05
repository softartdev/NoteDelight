package com.softartdev.notedelight.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.model.SettingsCategory
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsCategoriesViewModel(
    private val router: Router,
    private val adaptiveInteractor: AdaptiveInteractor,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<SettingsCategoriesResult> = MutableStateFlow(
        value = SettingsCategoriesResult()
    )
    val stateFlow: StateFlow<SettingsCategoriesResult> = mutableStateFlow

    private var job: Job? = null

    fun launchCategories() {
        if (job != null) return
        startCollectingSelection()
    }

    fun onAction(action: SettingsCategoriesAction) = when (action) {
        is SettingsCategoriesAction.SelectCategory -> selectCategory(action.category)
        is SettingsCategoriesAction.Refresh -> refresh()
        is SettingsCategoriesAction.NavBack -> navBack()
    }

    private fun selectCategory(category: SettingsCategory) = viewModelScope.launch {
        adaptiveInteractor.selectedSettingsCategoryIdStateFlow.value = category.id
        router.adaptiveNavigateToDetail(contentKey = category.id)
    }

    private fun refresh() = viewModelScope.launch {
        mutableStateFlow.update(SettingsCategoriesResult::showLoading)
        startCollectingSelection()
        mutableStateFlow.update { result ->
            result.copy(selectedCategoryId = adaptiveInteractor.selectedSettingsCategoryIdStateFlow.value)
        }
        mutableStateFlow.update(SettingsCategoriesResult::hideLoading)
    }

    private fun startCollectingSelection() {
        job?.cancel()
        job = viewModelScope.launch {
            adaptiveInteractor.selectedSettingsCategoryIdStateFlow.collect { selectedId: Long? ->
                mutableStateFlow.update { result -> result.copy(selectedCategoryId = selectedId) }
            }
        }
    }

    private fun navBack() {
        if (!router.popBackStack()) router.navigate(route = AppNavGraph.Splash)
    }
}
