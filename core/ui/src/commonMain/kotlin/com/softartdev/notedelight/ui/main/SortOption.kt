package com.softartdev.notedelight.ui.main

import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder
import notedelight.core.ui.generated.resources.Res
import notedelight.core.ui.generated.resources.sort_created_asc
import notedelight.core.ui.generated.resources.sort_created_desc
import notedelight.core.ui.generated.resources.sort_modified_asc
import notedelight.core.ui.generated.resources.sort_modified_desc
import notedelight.core.ui.generated.resources.sort_title_asc
import notedelight.core.ui.generated.resources.sort_title_desc
import org.jetbrains.compose.resources.StringResource

enum class SortOption(
    val noteSort: NoteSort,
    val labelRes: StringResource,
) {
    MODIFIED_DESC(NoteSort(SortField.DATE_MODIFIED, SortOrder.DESC), Res.string.sort_modified_desc),
    MODIFIED_ASC(NoteSort(SortField.DATE_MODIFIED, SortOrder.ASC), Res.string.sort_modified_asc),
    CREATED_DESC(NoteSort(SortField.DATE_CREATED, SortOrder.DESC), Res.string.sort_created_desc),
    CREATED_ASC(NoteSort(SortField.DATE_CREATED, SortOrder.ASC), Res.string.sort_created_asc),
    TITLE_ASC(NoteSort(SortField.TITLE, SortOrder.ASC), Res.string.sort_title_asc),
    TITLE_DESC(NoteSort(SortField.TITLE, SortOrder.DESC), Res.string.sort_title_desc),
}
