@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.feature.console.ui.ConsoleSurface
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleBuffer
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleBufferBuilder
import com.softartdev.notedelight.presentation.console.ConsoleAction
import com.softartdev.notedelight.presentation.console.ConsoleResult
import com.softartdev.notedelight.presentation.console.ConsoleViewModel
import com.softartdev.notedelight.util.CONSOLE_TIPS_BUTTON_TAG
import com.softartdev.notedelight.util.CONSOLE_TIP_AUTOFILL_PREFIX
import com.softartdev.notedelight.util.CONSOLE_TIP_COPY_PREFIX
import notedelight.core.ui.generated.resources.Res
import notedelight.core.ui.generated.resources.autofill
import notedelight.core.ui.generated.resources.console
import notedelight.core.ui.generated.resources.console_helper_text
import notedelight.core.ui.generated.resources.console_input_placeholder
import notedelight.core.ui.generated.resources.console_run
import notedelight.core.ui.generated.resources.console_tips
import notedelight.core.ui.generated.resources.copy
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConsoleScreen(
    onBackClick: () -> Unit,
    consoleViewModel: ConsoleViewModel = koinViewModel(),
) {
    val resultState: State<ConsoleResult> = consoleViewModel.stateFlow.collectAsState()
    ConsoleScreenBody(
        result = resultState.value,
        onBackClick = onBackClick,
        onAction = consoleViewModel::onAction,
    )
}

@Composable
fun ConsoleScreenBody(
    result: ConsoleResult = ConsoleResult(),
    onBackClick: () -> Unit = {},
    onAction: (ConsoleAction) -> Unit = {},
) {
    var tipsExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.console)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name,
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { tipsExpanded = true },
                            modifier = Modifier.testTag(CONSOLE_TIPS_BUTTON_TAG),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(Res.string.console_tips),
                            )
                        }
                        DropdownMenu(
                            expanded = tipsExpanded,
                            onDismissRequest = { tipsExpanded = false },
                        ) {
                            ConsoleTips.entries.forEachIndexed { index, tip ->
                                TipMenuItem(
                                    tip = tip.sql,
                                    index = index,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(tip.sql))
                                        tipsExpanded = false
                                    },
                                    onAutofill = {
                                        onAction(ConsoleAction.UpdateInput(tip.sql))
                                        tipsExpanded = false
                                    },
                                )
                                if (index < ConsoleTips.entries.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                },
            )
        },
    ) { paddingValues: PaddingValues ->
        ConsolePreferences(
            result = result,
            onAction = onAction,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun TipMenuItem(
    tip: String,
    index: Int,
    onCopy: () -> Unit,
    onAutofill: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = tip,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier.weight(1f).padding(start = 8.dp),
        )
        IconButton(
            onClick = onCopy,
            modifier = Modifier.testTag("$CONSOLE_TIP_COPY_PREFIX$index"),
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = stringResource(Res.string.copy),
            )
        }
        IconButton(
            onClick = onAutofill,
            modifier = Modifier.testTag("$CONSOLE_TIP_AUTOFILL_PREFIX$index"),
        ) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = stringResource(Res.string.autofill),
            )
        }
    }
}

@Composable
fun ConsolePreferences(
    result: ConsoleResult,
    onAction: (ConsoleAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buffer: ConsoleBuffer = remember(result.transcript) {
        ConsoleBufferBuilder.build(transcript = result.transcript)
    }
    val placeholder: String = stringResource(Res.string.console_input_placeholder)
    val runLabel: String = stringResource(Res.string.console_run)
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(Res.string.console_helper_text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (result.running) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        ConsoleSurface(
            buffer = buffer,
            inputText = result.input,
            running = result.running,
            runContentDescription = runLabel,
            placeholder = placeholder,
            onInputChange = { onAction(ConsoleAction.UpdateInput(it)) },
            onExecute = { onAction(ConsoleAction.Submit) },
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}
