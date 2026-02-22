@file:OptIn(ExperimentalWasmJsInterop::class, ExperimentalTestApi::class)

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
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiTestModules
import com.softartdev.notedelight.ui.cases.BackupFeatureTestCase
import com.softartdev.notedelight.ui.cases.CreateNoteWhileSelectedTestCase
import com.softartdev.notedelight.ui.cases.CrudTestCase
import com.softartdev.notedelight.ui.cases.EditTitleAfterCreateTestCase
import com.softartdev.notedelight.ui.cases.EditTitleAfterSaveTestCase
import com.softartdev.notedelight.ui.cases.FlowAfterCryptTestCase
import com.softartdev.notedelight.ui.cases.LocaleTestCase
import com.softartdev.notedelight.ui.cases.PrepopulateDbTestCase
import com.softartdev.notedelight.ui.cases.SettingPasswordTestCase
import com.softartdev.notedelight.util.kermitLogger
import kotlinx.coroutines.await
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.mp.KoinPlatformTools
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Web UI tests without inheritance from [CommonUiTests] to avoid issues with awaiting test results
 * on a Web platform. See [awaitComposeUiTest] function for details.
 * The version with inheritance from [CommonUiTests] in the end of the file.
 */
@Ignore
class WebUiTests {
    private val logger = Logger.withTag("ℹ️WebUiTests")
    private val modules: List<Module> = sharedModules + uiTestModules

    @BeforeTest
    fun setUp() {
        logger.i { "Setting up WebUiTests" }
        Logger.setLogWriters(platformLogWriter())
        when (KoinPlatformTools.defaultContext().getOrNull()) {
            null -> startKoin {
                kermitLogger()
                modules(modules)
            }
            else -> loadKoinModules(modules)
        }
    }

    @AfterTest
    fun tearDown() {
        logger.i { "Tearing down WebUiTests" }
        unloadKoinModules(modules)
        Logger.setLogWriters()
    }

    @Test
    fun crudNoteTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        CrudTestCase(composeUiTest = this@awaitComposeUiTest).invoke()
    }

    @Test
    fun createNoteWhileSelectedTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        CreateNoteWhileSelectedTestCase(composeUiTest = this@awaitComposeUiTest).invoke()
    }

    @Test
    fun editTitleAfterCreateTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        EditTitleAfterCreateTestCase(composeUiTest = this@awaitComposeUiTest).invoke()
    }

    @Test
    fun editTitleAfterSaveTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        EditTitleAfterSaveTestCase(composeUiTest = this@awaitComposeUiTest).invoke()
    }

    @Test
    fun prepopulateDatabase() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        PrepopulateDbTestCase(composeUiTest = this@awaitComposeUiTest).invoke()
    }

    @Test
    fun flowAfterCryptTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        FlowAfterCryptTestCase(
            composeUiTest = this@awaitComposeUiTest,
            pressBack = { clickBack(this@awaitComposeUiTest) },
            closeSoftKeyboard = ::closeSoftKeyboard
        ).invoke()
    }

    @Test
    fun settingPasswordTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        SettingPasswordTestCase(
            composeUiTest = this@awaitComposeUiTest,
            closeSoftKeyboard = ::closeSoftKeyboard
        ).invoke()
    }

    @Test
    fun localeTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        LocaleTestCase(
            composeUiTest = this@awaitComposeUiTest,
            pressBack = { clickBack(this@awaitComposeUiTest) }
        ).invoke()
    }

    @Test
    fun backupFeatureTest() = awaitComposeUiTest {
        launchApp(composeUiTest = this@awaitComposeUiTest)
        BackupFeatureTestCase(
            composeUiTest = this@awaitComposeUiTest,
            pressBack = { clickBack(this@awaitComposeUiTest) },
            closeSoftKeyboard = ::closeSoftKeyboard
        ).invoke()
    }

    private fun launchApp(composeUiTest: ComposeUiTest) {
        val lifecycleOwner = TestLifecycleOwner(initialState = Lifecycle.State.RESUMED)
        composeUiTest.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                App()
            }
        }
    }

    private fun awaitComposeUiTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        val testResult: TestResult = runTest { block() }
        testResult.await()
    }

    private fun clickBack(composeUiTest: ComposeUiTest) {
        composeUiTest.onNodeWithContentDescription(label = Icons.AutoMirrored.Filled.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()
    }

    private fun closeSoftKeyboard() = Unit // No-op on web
}
/*@Ignore class WebUiTests : CommonUiTests() {
    @BeforeTest override fun setUp() = super.setUp()
    @AfterTest override fun tearDown() = super.tearDown()
    @Test override fun crudNoteTest(): TestResult = super.crudNoteTest()
    @Test override fun createNoteWhileSelectedTest(): TestResult = super.createNoteWhileSelectedTest()
    @Test override fun editTitleAfterCreateTest(): TestResult = super.editTitleAfterCreateTest()
    @Test override fun editTitleAfterSaveTest(): TestResult = super.editTitleAfterSaveTest()
    @Test override fun prepopulateDatabase(): TestResult = super.prepopulateDatabase()
    @Test override fun flowAfterCryptTest(): TestResult = super.flowAfterCryptTest()
    @Test override fun settingPasswordTest(): TestResult = super.settingPasswordTest()
    @Test override fun localeTest(): TestResult = super.localeTest()
    @Test override fun backupFeatureTest(): TestResult = super.backupFeatureTest()
}*/
