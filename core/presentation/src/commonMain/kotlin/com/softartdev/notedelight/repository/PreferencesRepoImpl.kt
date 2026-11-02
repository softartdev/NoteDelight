package com.softartdev.notedelight.repository

import com.russhwolf.settings.Settings
import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder

class PreferencesRepoImpl(private val settings: Settings = Settings()) : PreferencesRepo {

    override var sortField: SortField
        get() = settings.getStringOrNull(SORT_FIELD_KEY)
            ?.let { saved -> SortField.entries.find { it.columnName == saved } }
            ?: SortField.DATE_MODIFIED
        set(value) = settings.putString(SORT_FIELD_KEY, value.columnName)

    override var sortOrder: SortOrder
        get() = settings.getStringOrNull(SORT_ORDER_KEY)
            ?.let { saved -> SortOrder.entries.find { it.sql == saved } }
            ?: SortOrder.DESC
        set(value) = settings.putString(SORT_ORDER_KEY, value.sql)

    companion object {
        const val SORT_FIELD_KEY = "note_sort_field"
        const val SORT_ORDER_KEY = "note_sort_order"
    }
}
