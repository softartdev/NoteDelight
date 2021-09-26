import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult
import di.AppModule
import di.AppModuleStub

@Composable
fun App(
    appModule: AppModule,
) {
    val mainViewModel: MainViewModel = appModule.mainViewModel
    val noteListState: State<NoteListResult> = mainViewModel.resultStateFlow.collectAsState()
    mainViewModel.updateNotes()
    MainScreen(noteListState, appModule)
}

@Composable
fun MainScreen(
    noteListState: State<NoteListResult>,
    appModule: AppModule,
) {
    val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
    MaterialTheme {
        when (val noteId: Long? = currentNoteIdState.value) {
            null -> when (val noteListResult = noteListState.value) {
                is NoteListResult.Loading -> Loader()
                is NoteListResult.Success -> NotesMain(noteListResult.result, currentNoteIdState)
                is NoteListResult.NavMain -> TODO()
                is NoteListResult.Error -> Error(err = noteListResult.error ?: "Error")
            }
            else -> NoteDetail(noteId, currentNoteIdState, appModule)
        }
    }
}

@Preview
@Composable
fun PreviewMain() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val noteListState: MutableState<NoteListResult> = remember {
        mutableStateOf(NoteListResult.Success(testNotes))
    }
    MainScreen(noteListState, AppModuleStub())
}