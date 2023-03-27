package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.util.SimpleDateFormat

@Composable
fun NoteItem(
    note: Note,
    onItemClicked: (id: Long) -> Unit,
) {
    Column(modifier = Modifier
        .padding(4.dp)
        .clickable { onItemClicked(note.id) }
        .clearAndSetSemantics { contentDescription = note.title }
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.h6,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = SimpleDateFormat.format(note.dateModified),
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewNoteItem() {
    NoteItem(
        note = TestSchema.firstNote,
        onItemClicked = {}
    )
}