@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight

import kotlinx.coroutines.test.TestResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class WebUiTests : CommonUiTests() {

    @BeforeTest
    override fun setUp() = super.setUp()

    @AfterTest
    override fun tearDown() = super.tearDown()

    @Test
    override fun crudNoteTest(): TestResult = super.crudNoteTest()

    @Test
    override fun editTitleAfterCreateTest(): TestResult = super.editTitleAfterCreateTest()

    @Test
    override fun editTitleAfterSaveTest(): TestResult = super.editTitleAfterSaveTest()

    @Test
    override fun prepopulateDatabase(): TestResult = super.prepopulateDatabase()

    @Test
    override fun flowAfterCryptTest(): TestResult = super.flowAfterCryptTest()

    @Test
    override fun settingPasswordTest(): TestResult = super.settingPasswordTest()

    @Test
    override fun localeTest(): TestResult = super.localeTest()
}