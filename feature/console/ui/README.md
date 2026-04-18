# feature:console:ui

A custom Compose-Multiplatform terminal surface for the SQL console. Targets `jvm`, `android`,
`iosArm64`, `iosSimulatorArm64`, and `wasmJs`.

The module hosts only **pure rendering primitives** — no string resources, no Koin, no
navigation. The screen frame (`Scaffold`, `TopAppBar`, tips dropdown, view-model wiring) lives
in `:ui:shared`, which resolves strings via `stringResource(...)` and passes them as plain
parameters into [`ConsoleSurface`](src/commonMain/kotlin/com/softartdev/notedelight/feature/console/ui/ConsoleSurface.kt).
This split keeps the new module resource-free and minimizes refactor churn.

## Public API

```kotlin
@Composable
fun ConsoleSurface(
    buffer: ConsoleBuffer,           // read-only history (built from ConsoleResult.transcript)
    inputText: String,                // current editable input
    running: Boolean,                 // true while a statement is executing
    runContentDescription: String,    // a11y label for the Run button (resolved string)
    placeholder: String,              // empty-input placeholder (resolved string)
    onInputChange: (String) -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier,
)
```

`ConsoleBufferBuilder.build(transcript)` maps the presentation-layer transcript into a renderable
`ConsoleBuffer`.

## Rendering strategy

A pure `Canvas` would force us to reimplement IME, caret, focus, and software-keyboard support
on every platform — a trap. Instead, the surface is a **hybrid**:

1. **History** is rendered by a single `Text` composable wrapped in a `SelectionContainer`. The
   whole buffer is flattened into one `AnnotatedString` with per-`ConsoleSegmentRole` color
   spans derived from `ConsoleTheme`. `SelectionContainer` wires platform-native
   tap-and-drag selection on every target (Android, iOS, desktop, wasmJs), so past commands
   and query output can be copied to the clipboard via the OS Copy affordance (long-press
   menu on touch, `Ctrl/Cmd+C` on desktop, context menu in the browser). `Text` populates
   `SemanticsProperties.Text` automatically; the composable additionally carries
   `contentDescription = buffer.plainText()` so screen readers and text-based test queries
   observe the whole scrollback.
2. **Active input** is a `BasicTextField` directly under the Canvas in the same scrolling
   `Column`, sharing typography and palette so it visually reads as the last line of the
   transcript. Compose's native caret handles selection and IME for free *within* the input.
3. The whole surface is wrapped in a Material3 `Surface` with `theme.surfaceColor` and a
   `theme.outlineColor` border — no hardcoded background, so it adapts cleanly to light and
   dark themes.

## Statement-completeness heuristic

`ConsoleStatementAnalyzer.isComplete(raw)`:

1. `trim()` first.
2. Empty → incomplete.
3. Starts with `.` → complete (dot-command).
4. Ends with `;` → complete.
5. Otherwise → incomplete.

Pressing Enter on a complete statement dispatches `ConsoleAction.Submit`; pressing Enter on an
incomplete one inserts a newline, which `ConsolePromptVisualTransformation` decorates with the
`    ...> ` continuation prompt.

The Run button submits unconditionally — it's the explicit affordance and preserves the existing
test contract.

## How to add a new `ConsoleSegmentRole`

1. Add the variant to [`ConsoleSegmentRole`](src/commonMain/kotlin/com/softartdev/notedelight/feature/console/ui/buffer/ConsoleSegmentRole.kt).
2. Add a matching color field to [`ConsoleTheme`](src/commonMain/kotlin/com/softartdev/notedelight/feature/console/ui/theme/ConsoleTheme.kt) and source it from `MaterialTheme.colorScheme` in `rememberConsoleTheme()`.
3. Wire the role in the `when` inside `ConsoleHistoryView.appendBufferLine(line, theme)`.
4. Update [`ConsoleBufferBuilder`](src/commonMain/kotlin/com/softartdev/notedelight/feature/console/ui/buffer/ConsoleBufferBuilder.kt) to emit the new role.

## Test tags

Surface-scoped tags are colocated with the composables that expose them:

```kotlin
const val CONSOLE_INPUT_FIELD_TAG = "CONSOLE_INPUT_FIELD_TAG"
const val CONSOLE_RUN_BUTTON_TAG  = "CONSOLE_RUN_BUTTON_TAG"
const val CONSOLE_TRANSCRIPT_TAG  = "CONSOLE_TRANSCRIPT_TAG"
```

Tips-menu tags (`CONSOLE_TIPS_BUTTON_TAG`, `CONSOLE_TIP_COPY_PREFIX`,
`CONSOLE_TIP_AUTOFILL_PREFIX`) stay in `com.softartdev.notedelight.util.TestTags` because the
tips dropdown lives in `:ui:shared`.

## Known limitations

- No SQL syntax highlighting — every `COMMAND` segment shares one color.
- `.help` / `.tables` / `.schema` dot-commands are forwarded to the data layer as-is and will
  error; a shell layer in `:feature:console:domain` is required to dispatch them.
- Up/Down arrow keys do not cycle `commandHistory` yet.
- Statement completeness is character-based — `;` inside string literals or comments isn't
  detected.
- `ConsolePromptVisualTransformation` operates on the string, not the layout, so long lines
  that wrap visually without an explicit `\n` do not trigger a continuation prompt.

## Roadmap

1. Up/Down arrow → `ConsoleAction.HistoryPrev` / `HistoryNext`.
2. Pluggable syntax-aware segmenter for `COMMAND` lines (keyword color).
3. Shell layer in `:feature:console:domain` recognising `.help`, `.tables`, `.schema`, `.clear`.
4. Stream multi-row / multi-column outputs (lift `safeRepo.execute(...)` from `String?` to a
   structured result).
5. Virtualize the history view (clip to viewport) once buffers grow large enough to matter.

## Tests

```bash
./gradlew :feature:console:ui:jvmTest
```

Three test files cover the pure pieces:
- `buffer/ConsoleBufferBuilderTest` — command splitting, role mapping, empty transcript.
- `input/ConsoleStatementAnalyzerTest` — trim handling, `;` tail, `.` prefix, blank, multi-line.
- `input/ConsolePromptVisualTransformationTest` — offset-mapping round-trip and edge cases.
