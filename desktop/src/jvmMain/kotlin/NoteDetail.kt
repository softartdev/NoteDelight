import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NoteDetail(
    noteId: Long,
    onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit
) {
    val noteUiState: MutableState<UiState<Note>> = uiStateFrom(noteId) { callback: (StateResult<Note>) -> Unit ->
        onLoadNote(noteId, callback)
    }
    when (val uiState: UiState<Note> = noteUiState.value) {
        is UiState.Loading -> Loader()
        is UiState.Success -> NoteDetailBody(uiState.data)
        is UiState.Error -> Error(err = uiState.exception.message ?: "Error")
    }
}

@Composable
fun NoteDetailBody(note: Note) {
    Text(note.text)
}

@Preview
@Composable
fun PreviewNoteDetailBody() {
    NoteDetailBody(note = TestSchema.firstNote)
}