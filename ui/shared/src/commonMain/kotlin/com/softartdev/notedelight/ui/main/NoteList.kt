package com.softartdev.notedelight.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.softartdev.notedelight.db.TestSchema.firstNote
import com.softartdev.notedelight.db.TestSchema.secondNote
import com.softartdev.notedelight.db.TestSchema.thirdNote
import com.softartdev.notedelight.model.Note
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoteList(
    pagingItems: LazyPagingItems<Note>,
    onItemClicked: (id: Long) -> Unit,
    selectedNoteId: Long?
) {
    LazyColumn {
        items(count = pagingItems.itemCount) { index ->
            when (val note: Note? = pagingItems[index]) {
                null -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                else -> NoteItem(note = note, onItemClicked = onItemClicked, selected = note.id == selectedNoteId)
            }
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
fun PreviewNoteList() = Surface {
    val lorem = StringBuilder().apply { repeat(100) { append("lorem ipsum ") } }.toString()
    val longTitleNote = secondNote.copy(title = lorem)
    val testNotes: List<Note> = listOf(firstNote, secondNote, thirdNote, longTitleNote)
    val pagingItems = flowOf(PagingData.from(testNotes)).collectAsLazyPagingItems()
    val onItemClicked: (id: Long) -> Unit = {}
    NoteList(pagingItems, onItemClicked, thirdNote.id)
}
