import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NoteItem(
    note: Note,
    onItemClicked: (id: Long) -> Unit
) {
    Column(modifier = Modifier.clickable { onItemClicked(note.id) }) {
        Text(note.text)
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