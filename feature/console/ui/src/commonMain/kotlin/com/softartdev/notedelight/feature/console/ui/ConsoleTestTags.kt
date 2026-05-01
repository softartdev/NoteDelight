package com.softartdev.notedelight.feature.console.ui

/**
 * Test tags for the console surface composables. Colocated with the composables that expose
 * them so `:core:ui` does not have to depend on console-internal identifiers.
 *
 * Tips-menu tags (`CONSOLE_TIPS_BUTTON_TAG`, `CONSOLE_TIP_COPY_PREFIX`,
 * `CONSOLE_TIP_AUTOFILL_PREFIX`) stay in `com.softartdev.notedelight.util.TestTags` because the
 * tips dropdown lives in `:core:ui`.
 */
const val CONSOLE_INPUT_FIELD_TAG: String = "CONSOLE_INPUT_FIELD_TAG"
const val CONSOLE_RUN_BUTTON_TAG: String = "CONSOLE_RUN_BUTTON_TAG"
const val CONSOLE_TRANSCRIPT_TAG: String = "CONSOLE_TRANSCRIPT_TAG"
