import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbRepo
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.flow.collect

fun main() = application {
    val dbRepo: DatabaseRepo = JdbcDbRepo()
    val noteUseCase = NoteUseCase(dbRepo)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Note Delight",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
        val noteListUiState: MutableState<UiState<List<Note>>> = uiStateFrom(null) { callback ->
            try {
                noteUseCase.getNotes().collect { notes: List<Note> ->
                    callback(StateResult.Success(notes))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(StateResult.Error(e))
            }
        }
        val onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit = { noteId, callback ->
            val stateResult: StateResult<Note> = try {
                val note: Note = noteUseCase.loadNote(noteId)
                StateResult.Success(note)
            } catch (e: Exception) {
                e.printStackTrace()
                StateResult.Error(e)
            }
            callback(stateResult)
        }
        App(currentNoteIdState, noteListUiState, onLoadNote)
    }
}

@Composable
fun App(
    currentNoteIdState: MutableState<Long?>,
    noteListState: MutableState<UiState<List<Note>>>,
    onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit,
) {
    MaterialTheme {
        when (val noteId: Long? = currentNoteIdState.value) {
            null -> when (val uiState: UiState<List<Note>> = noteListState.value) {
                is UiState.Loading -> Loader()
                is UiState.Success -> when {
                    uiState.data.isEmpty() -> Empty()
                    else -> NoteList(
                        noteList = uiState.data,
                        onItemClicked = { id ->
                            currentNoteIdState.value = id
                        }
                    )
                }
                is UiState.Error -> Error(err = uiState.exception.message ?: "Error")
            }
            else -> NoteDetail(noteId, onLoadNote, currentNoteIdState)
        }
    }
}

@Preview
@Composable
fun PreviewMain() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
    val noteListState: MutableState<UiState<List<Note>>> = remember {
        mutableStateOf(UiState.Success(testNotes))
    }
    val onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit = { noteId, callback ->
        val stateResult = try {
            val note: Note = requireNotNull(value = testNotes.find { it.id == noteId })
            StateResult.Success(note)
        } catch (e: Exception) {
            StateResult.Error(e)
        }
        callback(stateResult)
    }
    App(currentNoteIdState, noteListState, onLoadNote)
}