package com.softartdev.notedelight.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.util.DateTimeFormatter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoteItem(
    note: Note,
    onItemClicked: (id: Long) -> Unit,
) {
    ListItem(
        modifier = Modifier
            .clickable { onItemClicked(note.id) }
            .clearAndSetSemantics { contentDescription = note.title },
        overlineContent = {
            Text(text = DateTimeFormatter.format(note.dateModified), maxLines = 1)
        },
        headlineContent = { Text(text = note.title, maxLines = 2) },
    )
}

@Preview
@Composable
fun PreviewNoteItem() = Surface {
    NoteItem(note = TestSchema.firstNote, onItemClicked = {})
}