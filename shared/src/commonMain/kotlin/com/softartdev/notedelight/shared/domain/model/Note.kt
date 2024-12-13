package com.softartdev.notedelight.shared.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long,
    val title: String,
    val text: String,
    val dateCreated: LocalDateTime,
    val dateModified: LocalDateTime
)
