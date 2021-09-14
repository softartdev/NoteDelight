import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.shared.database.TestSchema
import com.softartdev.notedelight.shared.db.Note

@Composable
fun NotesMain(
    notes: List<Note>,
    currentNoteIdState: MutableState<Long?>,
    modifier: Modifier = Modifier.fillMaxHeight(),
) {
    Box(modifier = modifier) {
        NoteList(
            noteList = notes,
            onItemClicked = { id ->
                currentNoteIdState.value = id
            }
        )
        FloatingActionButton(
            onClick = { currentNoteIdState.value = 0 },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add note")
        }
    }
}

@Preview
@Composable
fun PreviewNotesMain() {
    val testNotes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)
    val currentNoteIdState: MutableState<Long?> = remember { mutableStateOf(null) }
    NotesMain(testNotes, currentNoteIdState, Modifier.fillMaxHeight(fraction = 0.5f))
}