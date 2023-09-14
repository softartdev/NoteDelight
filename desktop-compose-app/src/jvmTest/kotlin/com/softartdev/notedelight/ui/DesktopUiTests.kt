package com.softartdev.notedelight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.softartdev.notedelight.RootComponent
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.shared.runOnUiThread
import io.github.aakira.napier.Napier
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
            modules(allModules)
        }
        val lifecycle = LifecycleRegistry()
        val root = runOnUiThread {
            RootComponent(componentContext = DefaultComponentContext(lifecycle))
        }
        val dbRepo: DatabaseRepo = get(DatabaseRepo::class.java)
        dbRepo.noteQueries.deleteAll()
        super.setUp()
        composeTestRule.setContent { MainRootUI(root) }
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
        composeTestRule.onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
    }

    override fun closeSoftKeyboard() = Unit // Desktop doesn't have soft keyboard
}
