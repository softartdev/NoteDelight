import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.softartdev.notedelight.shared.database.TestSchema

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Note Delight",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        App()
    }
}

@Composable
fun App() {
    MaterialTheme {
        NoteList(
            noteList = listOf(TestSchema.firstNote,
                TestSchema.secondNote,
                TestSchema.thirdNote),//TODO replace with real
            onItemClicked = {}//TODO replace with real
        )
    }
}

@Preview
@Composable
fun PreviewMain() {
    App()
}