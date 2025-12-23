@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import com.softartdev.notedelight.App
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiTestModules
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.kermitLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.java.KoinJavaComponent.get
import java.io.File

class DesktopUiTests : AbstractJvmUiTests() {

    @get:Rule
    override val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Before
    override fun setUp() = runTest {
        super.setUp()
        Logger.setLogWriters(platformLogWriter())
        when (GlobalContext.getKoinApplicationOrNull()) {
            null -> startKoin {
                kermitLogger()
                modules(sharedModules + uiTestModules)
            }
            else -> loadKoinModules(sharedModules + uiTestModules)
        }
        val safeRepo: SafeRepo = get(SafeRepo::class.java)
        safeRepo.closeDatabase()
        File(safeRepo.dbPath).takeIf(File::exists)?.delete()
        safeRepo.buildDbIfNeed()
        val noteDAO: NoteDAO = get(NoteDAO::class.java)
        noteDAO.deleteAll()
        val lifecycleOwner = TestLifecycleOwner(
            initialState = Lifecycle.State.RESUMED,
            coroutineDispatcher = Dispatchers.Swing
        )
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                App()
            }
        }
        composeTestRule.waitForIdle()
    }

    @After
    override fun tearDown() = runTest {
        super.tearDown()
        unloadKoinModules(sharedModules + uiTestModules)
        Logger.setLogWriters()
    }

    @Test
    override fun crudNoteTest() = super.crudNoteTest()

    @Test
    override fun editTitleAfterCreateTest() = super.editTitleAfterCreateTest()

    @Test
    override fun editTitleAfterSaveTest() = super.editTitleAfterSaveTest()

    @Test
    override fun prepopulateDatabase() = super.prepopulateDatabase()

    @Test
    override fun flowAfterCryptTest() = super.flowAfterCryptTest()

    @Test
    override fun settingPasswordTest() = super.settingPasswordTest()

    @Test
    override fun localeTest() = super.localeTest()

    override fun pressBack() {
        composeTestRule.onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
    }

    override fun closeSoftKeyboard() = Unit // Desktop doesn't have soft keyboard
}
