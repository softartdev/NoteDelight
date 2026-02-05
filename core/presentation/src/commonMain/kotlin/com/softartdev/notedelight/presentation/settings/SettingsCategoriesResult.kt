package com.softartdev.notedelight.presentation.settings

import com.softartdev.notedelight.model.SettingsCategory

data class SettingsCategoriesResult(
    val selectedCategoryId: Long? = null,
    val loading: Boolean = false,
) {
    fun showLoading(): SettingsCategoriesResult = copy(loading = true)
    fun hideLoading(): SettingsCategoriesResult = copy(loading = false)
}

sealed interface SettingsCategoriesAction {
    data class SelectCategory(val category: SettingsCategory) : SettingsCategoriesAction
    data object Refresh : SettingsCategoriesAction
    data object NavBack : SettingsCategoriesAction
}
