@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.softartdev.notedelight.ui.cases.CrudTestCase
import com.softartdev.notedelight.ui.cases.CreateNoteWhileSelectedTestCase
import com.softartdev.notedelight.ui.cases.BackupFeatureTestCase
import com.softartdev.notedelight.ui.cases.EditTitleAfterCreateTestCase
import com.softartdev.notedelight.ui.cases.EditTitleAfterSaveTestCase
import com.softartdev.notedelight.ui.cases.FlowAfterCryptTestCase
import com.softartdev.notedelight.ui.cases.LocaleTestCase
import com.softartdev.notedelight.ui.cases.PrepopulateDbTestCase
import com.softartdev.notedelight.ui.cases.SettingPasswordTestCase

/**
 * Abstract class for UI test classes:
 * - AbstractJvmUiTests -> AndroidUiTests & DesktopUiTests
 * - [CommonUiTests] -> IosUiTests & WebUiTests
 */
abstract class AbstractUITests {
    abstract val composeUiTest: ComposeUiTest

    open fun setUp() = Unit

    open fun tearDown() = Unit

    open fun crudNoteTest() = CrudTestCase(composeUiTest).invoke()

    open fun createNoteWhileSelectedTest() = CreateNoteWhileSelectedTestCase(composeUiTest).invoke()

    open fun editTitleAfterCreateTest() = EditTitleAfterCreateTestCase(composeUiTest).invoke()

    open fun editTitleAfterSaveTest() = EditTitleAfterSaveTestCase(composeUiTest).invoke()

    open fun prepopulateDatabase() = PrepopulateDbTestCase(composeUiTest).invoke()

    open fun flowAfterCryptTest() = FlowAfterCryptTestCase(
        composeUiTest = composeUiTest,
        pressBack = ::pressBack,
        closeSoftKeyboard = ::closeSoftKeyboard
    ).invoke()

    open fun settingPasswordTest() = SettingPasswordTestCase(
        composeUiTest = composeUiTest,
        closeSoftKeyboard = ::closeSoftKeyboard
    ).invoke()

    open fun localeTest() = LocaleTestCase(
        composeUiTest = composeUiTest,
        pressBack = ::pressBack
    ).invoke()

    open fun backupFeatureTest() = BackupFeatureTestCase(
        composeUiTest = composeUiTest,
        pressBack = ::pressBack,
        closeSoftKeyboard = ::closeSoftKeyboard
    ).invoke()

    abstract fun pressBack()
    abstract fun closeSoftKeyboard()
}
