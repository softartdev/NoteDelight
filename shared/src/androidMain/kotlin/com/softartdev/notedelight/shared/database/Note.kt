package com.softartdev.notedelight.shared.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity
@TypeConverters(NoteTypeConverters::class)
data class Note(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val title: String,
        val text: String,
        val dateCreated: Date,
        var dateModified: Date
)
