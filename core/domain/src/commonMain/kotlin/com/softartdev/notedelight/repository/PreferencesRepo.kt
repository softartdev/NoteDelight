package com.softartdev.notedelight.repository

import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder

interface PreferencesRepo {
    var sortField: SortField
    var sortOrder: SortOrder
}
