package com.softartdev.notedelight.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.ui.selectedListItemColor
import com.softartdev.notedelight.util.BooleanPreviewProvider
import com.softartdev.notedelight.util.DateTimeFormatter

@Composable
fun NoteItem(
    note: Note,
    onItemClicked: (id: Long) -> Unit,
    selected: Boolean
) {
    ListItem(
        modifier = Modifier
            .clickable { onItemClicked(note.id) }
            .clearAndSetSemantics { contentDescription = note.title },
        overlineContent = {
            Text(text = DateTimeFormatter.format(note.dateModified), maxLines = 1)
        },
        headlineContent = { Text(text = note.title, maxLines = 2) },
        colors = ListItemDefaults.colors(containerColor = selectedListItemColor(selected)),
    )
}

@Preview
@Composable
fun PreviewNoteItem(
    @PreviewParameter(BooleanPreviewProvider::class) selected: Boolean
) = Surface {
    NoteItem(note = TestSchema.firstNote, onItemClicked = {}, selected = selected)
}
