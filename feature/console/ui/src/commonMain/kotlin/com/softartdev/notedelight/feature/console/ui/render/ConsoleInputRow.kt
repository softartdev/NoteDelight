package com.softartdev.notedelight.feature.console.ui.render

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.feature.console.ui.CONSOLE_INPUT_FIELD_TAG
import com.softartdev.notedelight.feature.console.ui.CONSOLE_RUN_BUTTON_TAG
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleBufferBuilder
import com.softartdev.notedelight.feature.console.ui.input.ConsolePromptVisualTransformation
import com.softartdev.notedelight.feature.console.ui.input.ConsoleStatementAnalyzer
import com.softartdev.notedelight.feature.console.ui.theme.ConsoleTheme

/**
 * The active, editable last line of the terminal. A leading `sqlite> ` prompt is rendered as a
 * sibling [Text]; continuation prompts on wrapped/newline sub-lines are injected by
 * [ConsolePromptVisualTransformation] so the caret can still sit at column 0 of the real
 * input. A trailing Run [IconButton] preserves the existing test contract and gives an
 * explicit submit affordance for users who prefer it over Enter.
 *
 * Enter handling:
 *   - On a complete statement (as decided by [ConsoleStatementAnalyzer]) pressing Enter
 *     triggers [onExecute] and the newline is *not* committed to the text.
 *   - On an incomplete statement the newline is accepted, growing the input to a second
 *     visual line with a continuation prompt.
 *
 * The detection is done in [onValueChange] by looking for exactly a single `\n` appended at
 * the end; this works uniformly for hardware keyboards, Android IME, iOS, and wasmJs.
 */
@Composable
internal fun ConsoleInputRow(
    inputText: String,
    running: Boolean,
    theme: ConsoleTheme,
    focusRequester: FocusRequester,
    runContentDescription: String,
    placeholder: String,
    onInputChange: (String) -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val continuationStyle: SpanStyle = remember(theme) {
        SpanStyle(color = theme.continuationPromptColor)
    }
    val visualTransformation = remember(continuationStyle) {
        ConsolePromptVisualTransformation(
            continuationPrompt = ConsoleBufferBuilder.CONTINUATION_PROMPT_TEXT,
            continuationPromptStyle = continuationStyle,
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = ConsoleBufferBuilder.PROMPT_TEXT,
            style = theme.textStyle.copy(color = theme.promptColor),
            modifier = Modifier.padding(top = 4.dp),
        )
        BasicTextField(
            value = inputText,
            onValueChange = { newValue ->
                val submitOnEnter: Boolean = newValue.endsWith(suffix = "\n") &&
                    newValue.length == inputText.length + 1 &&
                    newValue.dropLast(n = 1) == inputText &&
                    ConsoleStatementAnalyzer.isComplete(inputText)
                if (submitOnEnter) {
                    onExecute()
                } else {
                    onInputChange(newValue)
                }
            },
            modifier = Modifier
                .weight(weight = 1f)
                .padding(top = 4.dp)
                .focusRequester(focusRequester)
                .testTag(CONSOLE_INPUT_FIELD_TAG),
            textStyle = theme.textStyle.copy(color = theme.inputColor),
            cursorBrush = SolidColor(value = theme.caretColor),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(), // defaults: multiline-friendly
            keyboardActions = KeyboardActions(
                onAny = {
                    if (ConsoleStatementAnalyzer.isComplete(inputText)) onExecute()
                },
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (inputText.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = theme.textStyle.copy(color = theme.inputColor.copy(alpha = 0.4f)),
                        )
                    }
                    innerTextField()
                }
            },
        )
        IconButton(
            onClick = onExecute,
            enabled = inputText.isNotBlank() && !running,
            modifier = Modifier
                .width(width = 40.dp)
                .testTag(CONSOLE_RUN_BUTTON_TAG),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = runContentDescription,
                tint = theme.promptColor,
            )
        }
    }
}
