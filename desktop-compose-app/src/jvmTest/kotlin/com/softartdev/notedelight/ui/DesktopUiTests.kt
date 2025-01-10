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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.softartdev.notedelight.App
import com.softartdev.notedelight.TestLifecycleOwner
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiTestModules
import com.softartdev.notedelight.navigation.Router
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.get

class DesktopUiTests : AbstractUiTests() {

    @get:Rule
    override val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Before
    override fun setUp() {
        startKoin {
            printLogger(level = Level.DEBUG)
            modules(sharedModules + uiTestModules)
        }
        val router: Router = get(Router::class.java)
        val noteDAO: NoteDAO = get(NoteDAO::class.java)
        noteDAO.deleteAll()
        super.setUp()
        val lifecycleOwner = TestLifecycleOwner(coroutineDispatcher = Dispatchers.Swing)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                App(router)
            }
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()
        stopKoin()
        Napier.takeLogarithm()
    }

    @Test
    override fun crudNoteTest() = super.crudNoteTest()

    @Test
    override fun editTitleAfterCreateTest() = super.editTitleAfterCreateTest()

    @Test
    override fun editTitleAfterSaveTest() = super.editTitleAfterSaveTest()

    @Test
    override fun prepopulateDatabase() = super.prepopulateDatabase()

    @Ignore("Desktop app doesn't support encryption yet")
    @Test
    override fun flowAfterCryptTest() = super.flowAfterCryptTest()

    @Ignore("Desktop app doesn't support encryption yet")
    @Test
    override fun settingPasswordTest() = super.settingPasswordTest()

    override fun pressBack() {
        composeTestRule.onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
    }

    override fun closeSoftKeyboard() = Unit // Desktop doesn't have soft keyboard
}
