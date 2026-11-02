package com.softartdev.notedelight.repository

import com.russhwolf.settings.MapSettings
import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesRepoImplTest {

    @Test
    fun defaultSortIsDateModifiedDescending() {
        val repo = PreferencesRepoImpl(MapSettings())
        assertEquals(SortField.DATE_MODIFIED, repo.sortField)
        assertEquals(SortOrder.DESC, repo.sortOrder)
    }

    @Test
    fun persistsAndRestoresSortFieldAndOrderUsingColumnNameAndSql() {
        val settings = MapSettings()
        val repo = PreferencesRepoImpl(settings)

        repo.sortField = SortField.TITLE
        repo.sortOrder = SortOrder.ASC

        val reloadedRepo = PreferencesRepoImpl(settings)
        assertEquals(SortField.TITLE, reloadedRepo.sortField)
        assertEquals(SortOrder.ASC, reloadedRepo.sortOrder)
        assertEquals(SortField.TITLE.columnName, settings.getStringOrNull(PreferencesRepoImpl.SORT_FIELD_KEY))
        assertEquals(SortOrder.ASC.sql, settings.getStringOrNull(PreferencesRepoImpl.SORT_ORDER_KEY))
    }

    @Test
    fun overwritesExistingSortSettings() {
        val settings = MapSettings(
            PreferencesRepoImpl.SORT_FIELD_KEY to SortField.DATE_CREATED.columnName,
            PreferencesRepoImpl.SORT_ORDER_KEY to SortOrder.ASC.sql,
        )
        val repo = PreferencesRepoImpl(settings)
        assertEquals(SortField.DATE_CREATED, repo.sortField)
        assertEquals(SortOrder.ASC, repo.sortOrder)

        repo.sortField = SortField.DATE_MODIFIED
        repo.sortOrder = SortOrder.DESC
        assertEquals(SortField.DATE_MODIFIED, repo.sortField)
        assertEquals(SortOrder.DESC, repo.sortOrder)
        assertEquals(SortField.DATE_MODIFIED.columnName, settings.getStringOrNull(PreferencesRepoImpl.SORT_FIELD_KEY))
        assertEquals(SortOrder.DESC.sql, settings.getStringOrNull(PreferencesRepoImpl.SORT_ORDER_KEY))
    }
}
