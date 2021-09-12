import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NoteDetail(
    noteId: Long,
    onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit,
    currentNoteIdState: MutableState<Long?>
) {
    val noteUiState: MutableState<UiState<Note>> = uiStateFrom(noteId) { callback: (StateResult<Note>) -> Unit ->
        onLoadNote(noteId, callback)
    }
    when (val uiState: UiState<Note> = noteUiState.value) {
        is UiState.Loading -> Loader()
        is UiState.Success -> NoteDetailBody(uiState.data, currentNoteIdState)
        is UiState.Error -> Error(err = uiState.exception.message ?: "Error")
    }
}

@Composable
fun NoteDetailBody(
    note: Note,
    currentNoteIdState: MutableState<Long?>,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = { Text(note.title) },
            navigationIcon = {
                IconButton(onClick = { currentNoteIdState.value = null }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
        var text by remember { mutableStateOf(note.text) }
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text("Type text here") },
        )
    }
}

@Preview
@Composable
fun PreviewNoteDetailBody() {
    val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
    NoteDetailBody(TestSchema.firstNote, currentNoteIdState)
}