@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiTestModules
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.kermitLogger
import kotlinx.coroutines.test.TestResult
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.mp.KoinPlatformTools

/**
 * Common UI tests for iOS and Web platforms.
 */
abstract class CommonUiTests : AbstractUITests() {

    private var _composeUiTest: ComposeUiTest? = null

    override val composeUiTest: ComposeUiTest
        get() = requireNotNull(_composeUiTest)

    override fun setUp() {
        super.setUp()
        Logger.setLogWriters(platformLogWriter())
        when (KoinPlatformTools.defaultContext().getOrNull()) {
            null -> startKoin {
                kermitLogger()
                modules(sharedModules + uiTestModules)
            }
            else -> loadKoinModules(sharedModules + uiTestModules)
        }
    }

    private suspend fun beforeTest() {
        val safeRepo: SafeRepo = KoinPlatformTools.defaultContext().get().get(SafeRepo::class)
        safeRepo.closeDatabase()
        safeRepo.deleteDatabase()
        safeRepo.buildDbIfNeed()
        val noteDAO: NoteDAO = KoinPlatformTools.defaultContext().get().get(NoteDAO::class)
        noteDAO.deleteAll()
        val lifecycleOwner = TestLifecycleOwner(initialState = Lifecycle.State.RESUMED)
        composeUiTest.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                App()
            }
        }
        composeUiTest.waitForIdle()
    }

    override fun tearDown() {
        super.tearDown()
        unloadKoinModules(sharedModules + uiTestModules)
        Logger.setLogWriters()
    }

    override fun crudNoteTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.crudNoteTest()
    }

    override fun editTitleAfterCreateTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.editTitleAfterCreateTest()
    }

    override fun editTitleAfterSaveTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.editTitleAfterSaveTest()
    }

    override fun prepopulateDatabase(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.prepopulateDatabase()
    }

    override fun flowAfterCryptTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.flowAfterCryptTest()
    }

    override fun settingPasswordTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.settingPasswordTest()
    }

    override fun localeTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.localeTest()
    }

    override fun pressBack() {
        composeUiTest.onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
    }

    override fun closeSoftKeyboard() = Unit // No-op on web
}
