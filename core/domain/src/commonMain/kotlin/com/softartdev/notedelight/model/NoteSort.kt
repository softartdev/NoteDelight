package com.softartdev.notedelight.model

enum class SortField(val columnName: String) {
    DATE_MODIFIED("dateModified"),
    DATE_CREATED("dateCreated"),
    TITLE("title"),
}

enum class SortOrder(val sql: String) {
    ASC("ASC"),
    DESC("DESC"),
}

data class NoteSort(
    val field: SortField = SortField.DATE_MODIFIED,
    val order: SortOrder = SortOrder.DESC,
)
