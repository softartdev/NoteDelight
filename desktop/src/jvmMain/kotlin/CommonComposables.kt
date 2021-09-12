import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Loader() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun Empty() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "The list is empty",
            style = MaterialTheme.typography.h5,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Press + to add a note.",
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

@Composable
fun Error(err: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        Text(text = err, style = TextStyle(color = MaterialTheme.colors.error, fontWeight = FontWeight.Bold))
    }
}

@Preview
@Composable
fun PreviewLoader() {
    Loader()
}

@Preview
@Composable
fun PreviewEmpty() {
    Empty()
}

@Preview
@Composable
fun PreviewError() {
    Error(err = "Mock error")
}

@Preview
@Composable
fun PreviewCommons() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PreviewLoader()

        Divider()

        PreviewEmpty()

        Divider()

        PreviewError()
    }
}