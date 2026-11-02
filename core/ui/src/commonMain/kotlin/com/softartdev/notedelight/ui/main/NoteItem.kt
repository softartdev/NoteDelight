package com.softartdev.notedelight.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.ui.selectedListItemColor
import com.softartdev.notedelight.util.BooleanPreviewProvider
import com.softartdev.notedelight.util.DateTimeFormatter

@Composable
fun NoteItem(
    note: Note,
    onItemClicked: (id: Long) -> Unit,
    selected: Boolean,
) {
    ListItem(
        modifier = Modifier
            .clickable { onItemClicked(note.id) }
            .clearAndSetSemantics { contentDescription = note.title },
        overlineContent = {
            NoteItemDates(
                createdText = DateTimeFormatter.format(note.dateCreated),
                modifiedText = DateTimeFormatter.format(note.dateModified),
            )
        },
        headlineContent = { Text(text = note.title, maxLines = 2) },
        colors = ListItemDefaults.colors(containerColor = selectedListItemColor(selected)),
    )
}

@Composable
private fun NoteItemDates(createdText: String, modifiedText: String) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
) {
    if (createdText == modifiedText) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
        )
        Text(text = createdText, maxLines = 1)
    } else {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.Default.Edit,
            contentDescription = null,
        )
        Text(text = modifiedText, maxLines = 1)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.Default.AddCircleOutline,
            contentDescription = null,
        )
        Text(text = createdText, maxLines = 1)
    }
}

@Preview
@Composable
fun PreviewNoteItem(
    @PreviewParameter(BooleanPreviewProvider::class) selected: Boolean
) = Surface {
    val note: Note = if (selected) {
        TestSchema.firstNote
    } else {
        TestSchema.firstNote.copy(dateModified = TestSchema.firstNote.dateCreated)
    }
    NoteItem(note = note, onItemClicked = {}, selected = selected)
}
