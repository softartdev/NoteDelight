package com.softartdev.notedelight.model

import kotlinx.datetime.LocalDateTime

data class Note(
    val id: Long,
    val title: String,
    val text: String,
    val dateCreated: LocalDateTime,
    val dateModified: LocalDateTime,
)
