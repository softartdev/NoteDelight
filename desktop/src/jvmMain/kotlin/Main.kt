import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbRepo
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    Napier.base(antilog = DebugAntilog())

    val dbRepo: DatabaseRepo = JdbcDbRepo()
    val noteUseCase = NoteUseCase(dbRepo)
    val mainViewModel = MainViewModel(noteUseCase)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Note Delight",
        state = rememberWindowState(width = 320.dp, height = 480.dp),
        icon = painterResource(resourcePath = "app_icon.png")
    ) {
        val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
        val onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit = { noteId, callback ->
            val stateResult: StateResult<Note> = try {
                val note: Note = noteUseCase.loadNote(noteId = when (noteId) {
                    0L -> noteUseCase.createNote()
                    else -> noteId
                })
                StateResult.Success(note)
            } catch (e: Exception) {
                e.printStackTrace()
                StateResult.Error(e)
            }
            callback(stateResult)
        }
        App(currentNoteIdState, mainViewModel, onLoadNote)
    }
}

@Composable
fun App(
    currentNoteIdState: MutableState<Long?>,
    mainViewModel: MainViewModel,
    onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit,
) {
    val noteListState: State<NoteListResult> = mainViewModel.resultStateFlow.collectAsState()
    mainViewModel.updateNotes()
    MainScreen(currentNoteIdState, noteListState, onLoadNote)
}

@Composable
fun MainScreen(
    currentNoteIdState: MutableState<Long?>,
    noteListState: State<NoteListResult>,
    onLoadNote: suspend (Long, (StateResult<Note>) -> Unit) -> Unit,
) {
    MaterialTheme {
        when (val noteId: Long? = currentNoteIdState.value) {
            null -> when (val noteListResult = noteListState.value) {
                is NoteListResult.Loading -> Loader()
                is NoteListResult.Success -> NotesMain(noteListResult.result, currentNoteIdState)
                is NoteListResult.NavMain -> TODO()
                is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
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
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(testNotes))
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
    MainScreen(currentNoteIdState, noteListState, onLoadNote)
}