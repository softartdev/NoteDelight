package com.softartdev.notedelight.ui.settings.detail

import kotlinx.coroutines.test.TestResult
import kotlin.test.Test

class JvmConsoleScreenTest : ConsoleScreenTest() {

    @Test
    override fun tipsMenuShowsCopyAndAutofillBody(): TestResult = super.tipsMenuShowsCopyAndAutofillBody()

    @Test
    override fun tipsMenuCopyActionBody(): TestResult = super.tipsMenuCopyActionBody()

    @Test
    override fun runButtonDisabledWhenInputBlankBody(): TestResult = super.runButtonDisabledWhenInputBlankBody()

    @Test
    override fun inputFieldShowsAutofillValueBody(): TestResult = super.inputFieldShowsAutofillValueBody()
}
