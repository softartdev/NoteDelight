import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.JdbcDbRepo
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Note Delight",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        val scope = rememberCoroutineScope()

        val dbRepo: DatabaseRepo = JdbcDbRepo()

        val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
        val noteListState: MutableState<UiState<List<Note>>> =
            uiStateFrom(null) { callback: (StateResult<List<Note>>) -> Unit ->
                scope.launch {//TODO remove
                    delay(5000)//TODO remove
                    try {
                        callback(StateResult.Success(
                            data = dbRepo.noteQueries.getAll().executeAsList()
                        ))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(StateResult.Error(
                            exception = e
                        ))
                    }
                }
            }

        App(currentNoteIdState, noteListState)
    }
}

@Composable
fun App(
    currentNoteIdState: MutableState<Long?>,
    noteListState: MutableState<UiState<List<Note>>>,
) {
    MaterialTheme {
        noteListState.value.let { uiState: UiState<List<Note>> ->
            when (uiState) {
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
        }
    }
}

@Preview
@Composable
fun PreviewMain() {
    val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
    val noteListState: MutableState<UiState<List<Note>>> = remember {
        val data = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
        mutableStateOf(UiState.Success(data))
    }
    App(currentNoteIdState, noteListState)
}