package com.softartdev.notedelight.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.detail_pane_placeholder
import notedelight.ui.shared.generated.resources.label_empty_result
import notedelight.ui.shared.generated.resources.press_add_note
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
expect fun EnableEdgeToEdge()

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)

@Composable
fun MainDetailPanePlaceholder() = Card(shape = RoundedCornerShape(size = 0.dp)) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = Icons.Default.EditNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.detail_pane_placeholder),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun SettingsDetailPanePlaceholder() = Card(shape = RoundedCornerShape(size = 0.dp)) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(128.dp),
            imageVector = Icons.TwoTone.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

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

@Preview(showBackground = true)
@Composable
fun PreviewMainDetailPanePlaceholder() {
    MainDetailPanePlaceholder()
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsDetailPanePlaceholder() {
    SettingsDetailPanePlaceholder()
}

@Preview(showBackground = true)
@Composable
fun PreviewLoader() = Loader()

@Preview(showBackground = true)
@Composable
fun PreviewEmpty() = Empty()

@Preview(showBackground = true)
@Composable
fun PreviewError() = Error(err = "Mock error")