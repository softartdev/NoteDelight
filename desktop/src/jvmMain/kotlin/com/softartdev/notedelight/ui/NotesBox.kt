package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NotesBox(
    notes: List<Note>,
    onItemClicked: (id: Long) -> Unit, // Called on item click
    modifier: Modifier = Modifier.fillMaxHeight(),
) {
    Box(modifier = modifier) {
        when {
            notes.isEmpty() -> Empty()
            else -> NoteList(
                noteList = notes,
                onItemClicked = onItemClicked
            )
        }
        FloatingActionButton(
            onClick = { onItemClicked(0) },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add note")
        }
    }
}

@Preview
@Composable
fun PreviewNotesBox() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val onItemClicked: (id: Long) -> Unit = {}
    NotesBox(testNotes, onItemClicked, Modifier.fillMaxHeight(fraction = 0.5f))
}