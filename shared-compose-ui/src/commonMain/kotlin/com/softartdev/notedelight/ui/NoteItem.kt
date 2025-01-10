package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.db.model
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.util.DateTimeFormatter

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
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = DateTimeFormatter.format(note.dateModified),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewNoteItem() {
    NoteItem(
        note = TestSchema.firstNote.model,
        onItemClicked = {}
    )
}