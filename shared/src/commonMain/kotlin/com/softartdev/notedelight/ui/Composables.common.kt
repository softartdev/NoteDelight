package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.label_empty_result
import notedelight.shared.generated.resources.press_add_note
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)

@Composable
fun Loader(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize().padding(all = 20.dp)
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
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(Res.string.label_empty_result),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            text = stringResource(Res.string.press_add_note),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(3f))
    }
}

@Composable
fun Error(err: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        Text(text = err, style = TextStyle(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold))
    }
}

@Preview
@Composable
fun PreviewLoader() = Loader()

@Preview
@Composable
fun PreviewEmpty() = Empty()

@Preview
@Composable
fun PreviewError() = Error(err = "Mock error")

@Preview
@Composable
fun PreviewCommons() = Column(modifier = Modifier.fillMaxWidth()) {
    PreviewLoader()

    HorizontalDivider()

    PreviewEmpty()

    HorizontalDivider()

    PreviewError()
}