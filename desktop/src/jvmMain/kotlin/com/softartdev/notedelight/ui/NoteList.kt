package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NoteList(
    noteList: List<Note>,
    onItemClicked: (id: Long) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        items(noteList) {
            NoteItem(
                note = it,
                onItemClicked = onItemClicked,
            )

            Divider()
        }
    }
}

@Preview
@Composable
fun PreviewNoteList() {
    val lorem = StringBuilder().apply { repeat(100) { append("lorem ipsum ") } }.toString()
    val longTitleNote = TestSchema.secondNote.copy(title = lorem)
    val testNotes: List<Note> = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote, longTitleNote)
    val onItemClicked: (id: Long) -> Unit = {}
    NoteList(testNotes, onItemClicked)
}