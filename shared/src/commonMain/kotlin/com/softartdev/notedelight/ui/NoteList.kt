package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.softartdev.notedelight.db.TestSchema.firstNote
import com.softartdev.notedelight.db.TestSchema.secondNote
import com.softartdev.notedelight.db.TestSchema.thirdNote
import com.softartdev.notedelight.db.toModel
import com.softartdev.notedelight.model.Note
import kotlinx.coroutines.flow.flowOf

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

@Preview
@Composable
fun PreviewNoteList() {
    val lorem = StringBuilder().apply { repeat(100) { append("lorem ipsum ") } }.toString()
    val longTitleNote = secondNote.copy(title = lorem)
    val testNotes: List<Note> = listOf(firstNote, secondNote, thirdNote, longTitleNote).toModel()
    val pagingItems = flowOf(PagingData.from(testNotes)).collectAsLazyPagingItems()
    val onItemClicked: (id: Long) -> Unit = {}
    NoteList(pagingItems, onItemClicked)
}
