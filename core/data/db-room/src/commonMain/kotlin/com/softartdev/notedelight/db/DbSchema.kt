package com.softartdev.notedelight.db

import com.softartdev.notedelight.model.Note
import kotlinx.datetime.LocalDateTime

object TestSchema {

    val firstNote = Note(
        id = 1,
        title = "first title from test schema",
        text = "first text",
        dateCreated = LocalDateTime(2018, 1, 2, 3, 4),
        dateModified = LocalDateTime(2018, 2, 3, 4, 5),
    )
    val secondNote = Note(
        id = 2,
        title = "second title",
        text = "second text",
        dateCreated = LocalDateTime(2017, 1, 2, 3, 4),
        dateModified = LocalDateTime(2017, 2, 3, 4, 5),
    )
    val thirdNote = Note(
        id = 3,
        title = "third title",
        text = "third text",
        dateCreated = LocalDateTime(2016, 1, 2, 3, 4),
        dateModified = LocalDateTime(2016, 2, 3, 4, 5),
    )
    val notes = listOf(firstNote, secondNote, thirdNote)
}
