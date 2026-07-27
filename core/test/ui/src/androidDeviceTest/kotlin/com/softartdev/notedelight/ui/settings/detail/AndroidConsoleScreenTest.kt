package com.softartdev.notedelight.ui.settings.detail

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.TestResult
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class AndroidConsoleScreenTest : ConsoleScreenTest() {

    @Test
    override fun tipsMenuShowsCopyAndAutofillBody(): TestResult = super.tipsMenuShowsCopyAndAutofillBody()

    @Test
    override fun tipsMenuCopyActionBody(): TestResult = super.tipsMenuCopyActionBody()

    @Test
    override fun runButtonDisabledWhenInputBlankBody(): TestResult = super.runButtonDisabledWhenInputBlankBody()

    @Test
    override fun inputFieldShowsAutofillValueBody(): TestResult = super.inputFieldShowsAutofillValueBody()
}
