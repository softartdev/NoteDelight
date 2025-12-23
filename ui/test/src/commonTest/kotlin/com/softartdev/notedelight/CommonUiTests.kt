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
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class CommonUiTests : AbstractUITests() {

    private var _composeUiTest: ComposeUiTest? = null

    override val composeUiTest: ComposeUiTest
        get() = requireNotNull(_composeUiTest)

    private suspend fun beforeTest() {
        Logger.setLogWriters(platformLogWriter())
        when (KoinPlatformTools.defaultContext().getOrNull()) {
            null -> startKoin {
                kermitLogger()
                modules(sharedModules + uiTestModules)
            }
            else -> loadKoinModules(sharedModules + uiTestModules)
        }
        val safeRepo: SafeRepo = KoinPlatformTools.defaultContext().get().get(SafeRepo::class)
        safeRepo.closeDatabase()
        safeRepo.buildDbIfNeed()
        val noteDAO: NoteDAO = KoinPlatformTools.defaultContext().get().get(NoteDAO::class)
        noteDAO.deleteAll()
        super.setUp()
        val lifecycleOwner = TestLifecycleOwner(initialState = Lifecycle.State.RESUMED)
        composeUiTest.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                App()
            }
        }
        composeUiTest.waitForIdle()
    }

    private fun afterTest() {
        super.tearDown()
        unloadKoinModules(sharedModules + uiTestModules)
        Logger.setLogWriters()
    }

    @Test
    override fun crudNoteTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.crudNoteTest()
        afterTest()
    }

    @Test
    override fun editTitleAfterCreateTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.editTitleAfterCreateTest()
        afterTest()
    }

    @Test
    override fun editTitleAfterSaveTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.editTitleAfterSaveTest()
        afterTest()
    }

    @Test
    override fun prepopulateDatabase(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.prepopulateDatabase()
        afterTest()
    }

    @Test
    override fun flowAfterCryptTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.flowAfterCryptTest()
        afterTest()
    }

    @Test
    override fun settingPasswordTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.settingPasswordTest()
        afterTest()
    }

    @Test
    override fun localeTest(): TestResult = runComposeUiTest {
        _composeUiTest = this
        beforeTest()
        super.localeTest()
        afterTest()
    }

    override fun pressBack() {
        composeUiTest.onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
    }

    override fun closeSoftKeyboard() = Unit // No-op on web
}