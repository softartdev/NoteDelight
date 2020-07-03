package com.softartdev.notedelight.shared.database

import java.util.*

data class Note(
        val id: Long,
        val title: String,
        val text: String,
        val dateCreated: Date,
        var dateModified: Date
)
