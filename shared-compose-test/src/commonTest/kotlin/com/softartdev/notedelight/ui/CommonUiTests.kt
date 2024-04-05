@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import com.softartdev.notedelight.RootComponent
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.shared.util.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatformTools
import kotlin.coroutines.CoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.test.Ignore

class CommonUiTests : AbstractUiTests() {

    private var skikoComposeUiTest: SkikoComposeUiTest? = null
    private var decomposeRootComponent: RootComponent? = null

    override val composeTestRule: ComposeUiTest
        get() = requireNotNull(skikoComposeUiTest)

    @BeforeTest
    override fun setUp() {
        Napier.base(antilog = DebugAntilog())
        skikoComposeUiTest = SkikoComposeUiTest()
        startKoin {
            logger(NapierKoinLogger(Level.DEBUG))
            modules(allModules)
        }
        decomposeRootComponent = UiTestUtil.decomposeRootComponent
        val noteDAO: NoteDAO = KoinPlatformTools.defaultContext().get().get()
        noteDAO.deleteAll()
        super.setUp()
    }

    @AfterTest
    override fun tearDown() {
        super.tearDown()
        skikoComposeUiTest = null
        stopKoin()
        Napier.takeLogarithm()
    }

    @Test
    override fun crudNoteTest() {
        skikoComposeUiTest?.runTest {
            skikoComposeUiTest?.setContent { MainRootUI(decomposeRootComponent!!) }
            super.crudNoteTest()
        }
    }

    @Test
    override fun editTitleAfterCreateTest() {
        skikoComposeUiTest?.runTest {
            skikoComposeUiTest?.setContent { MainRootUI(decomposeRootComponent!!) }
            super.editTitleAfterCreateTest()
        }
    }

    @Test
    override fun editTitleAfterSaveTest() {
        skikoComposeUiTest?.runTest {
            skikoComposeUiTest?.setContent { MainRootUI(decomposeRootComponent!!) }
            super.editTitleAfterSaveTest()
        }
    }

    @Test
    override fun prepopulateDatabase(): TestResult = runTest {
        skikoComposeUiTest?.runTest {
            skikoComposeUiTest?.setContent { MainRootUI(decomposeRootComponent!!) }
            super.prepopulateDatabase()
        }
    }

    @Ignore
    @Test
    override fun flowAfterCryptTest() {
        skikoComposeUiTest?.runTest {
            skikoComposeUiTest?.setContent { MainRootUI(decomposeRootComponent!!) }
            super.flowAfterCryptTest()
        }
    }

    @Ignore
    @Test
    override fun settingPasswordTest() {
        skikoComposeUiTest?.runTest {
            skikoComposeUiTest?.setContent { MainRootUI(decomposeRootComponent!!) }
            super.settingPasswordTest()
        }
    }

    override fun pressBack() {}
    override fun closeSoftKeyboard() {}
}