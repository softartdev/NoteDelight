package com.softartdev.notedelight.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NoteList(
    pagingItems: LazyPagingItems<Note>,
    onItemClicked: (id: Long) -> Unit,
) {
    LazyColumn {
        items(count = pagingItems.itemCount) { index ->
            when (val note: Note? = pagingItems[index]) {
                null -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                else -> NoteItem(note = note, onItemClicked = onItemClicked)
            }
            HorizontalDivider()
        }
    }
}
/*TODO
@Preview
@Composable
fun PreviewNoteList() {
    val lorem = StringBuilder().apply { repeat(100) { append("lorem ipsum ") } }.toString()
    val longTitleNote = secondNote.copy(title = lorem)
    val testNotes: List<Note> = listOf(firstNote, secondNote, thirdNote, longTitleNote)
    val onItemClicked: (id: Long) -> Unit = {}
    NoteList(testNotes, onItemClicked)
}
*/