package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.softartdev.notedelight.shared.database.TestSchema.firstNote
import com.softartdev.notedelight.shared.database.TestSchema.secondNote
import com.softartdev.notedelight.shared.database.TestSchema.thirdNote
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NoteList(
    noteList: List<Note>,
    onItemClicked: (id: Long) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(modifier = Modifier.testTag(NOTE_LIST_TEST_TAG), state = listState) {
        items(items = noteList, key = Note::id) {
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
    val longTitleNote = secondNote.copy(title = lorem)
    val testNotes: List<Note> = listOf(firstNote, secondNote, thirdNote, longTitleNote)
    val onItemClicked: (id: Long) -> Unit = {}
    NoteList(testNotes, onItemClicked)
}

const val NOTE_LIST_TEST_TAG = "note_list_test_tag"