package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.softartdev.notedelight.ui.cases.CrudTestCase
import com.softartdev.notedelight.ui.cases.EditTitleAfterCreateTestCase
import com.softartdev.notedelight.ui.cases.EditTitleAfterSaveTestCase
import com.softartdev.notedelight.ui.cases.FlowAfterCryptTestCase
import com.softartdev.notedelight.ui.cases.PrepopulateDbTestCase
import com.softartdev.notedelight.ui.cases.SettingPasswordTestCase

abstract class AbstractUiTests {
    abstract val composeTestRule: ComposeContentTestRule

    open fun setUp() = Unit

    open fun tearDown() = Unit

    open fun crudNoteTest() = CrudTestCase(composeTestRule).invoke()

    open fun editTitleAfterCreateTest() = EditTitleAfterCreateTestCase(composeTestRule).invoke()

    open fun editTitleAfterSaveTest() = EditTitleAfterSaveTestCase(composeTestRule).invoke()

    open fun prepopulateDatabase() = PrepopulateDbTestCase(composeTestRule).invoke()

    open fun flowAfterCryptTest() = FlowAfterCryptTestCase(
        composeTestRule = composeTestRule,
        pressBack = ::pressBack,
        closeSoftKeyboard = ::closeSoftKeyboard
    ).invoke()

    open fun settingPasswordTest() = SettingPasswordTestCase(
        composeTestRule = composeTestRule,
        closeSoftKeyboard = ::closeSoftKeyboard
    ).invoke()

    abstract fun pressBack()
    abstract fun closeSoftKeyboard()
}