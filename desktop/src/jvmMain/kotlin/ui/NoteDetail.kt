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
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import di.AppModule

@Composable
fun NoteDetail(
    noteId: Long,
    currentNoteIdState: MutableState<Long?>,
    appModule: AppModule
) {
    val noteViewModel = appModule.noteViewModel
    when (noteId) {
        0L -> noteViewModel.createNote()
        else -> noteViewModel.loadNote(noteId)
    }
    val noteState: State<NoteResult> = noteViewModel.resultStateFlow.collectAsState()
    when (val noteResult: NoteResult = noteState.value) {
        is NoteResult.Loading -> Loader()
        is NoteResult.Loaded -> NoteDetailBody(noteResult.result, currentNoteIdState)
        is NoteResult.Error -> Error(err = noteResult.message ?: "Error")
        is NoteResult.CheckSaveChange -> TODO()
        is NoteResult.Created -> TODO()
        is NoteResult.Deleted -> TODO()
        is NoteResult.Empty -> TODO()
        is NoteResult.NavBack -> TODO()
        is NoteResult.NavEditTitle -> TODO()
        is NoteResult.Saved -> TODO()
        is NoteResult.TitleUpdated -> TODO()
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