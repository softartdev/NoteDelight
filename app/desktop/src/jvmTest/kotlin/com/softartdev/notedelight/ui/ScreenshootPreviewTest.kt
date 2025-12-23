package com.softartdev.notedelight.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
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
import com.softartdev.notedelight.util.DateTimeFormatter
import com.softartdev.notedelight.util.createLocalDateTime
import com.softartdev.notedelight.util.kermitLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.java.KoinJavaComponent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Ignore("Use this test to generate screenshots manually")
class ScreenshootPreviewTest {
    private val logger = Logger.withTag("ℹ️ScreenshootPreviewTest")

    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Before
    fun setUp() = runTest {
        Logger.setLogWriters(platformLogWriter())
        when (GlobalContext.getKoinApplicationOrNull()) {
            null -> startKoin {
                kermitLogger()
                modules(sharedModules + uiTestModules)
            }
            else -> loadKoinModules(sharedModules + uiTestModules)
        }
        val safeRepo: SafeRepo = KoinJavaComponent.get(SafeRepo::class.java)
        safeRepo.closeDatabase()
        File(safeRepo.dbPath).takeIf(File::exists)?.delete()
        safeRepo.buildDbIfNeed()
        val noteDAO: NoteDAO = KoinJavaComponent.get(NoteDAO::class.java)
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
    fun tearDown() = runTest {
        unloadKoinModules(sharedModules + uiTestModules)
        Logger.setLogWriters()
    }

    @Test
    fun saveScreenshootPreviews() {
        val screenshotFile = captureScreenshot("app_preview")
        logger.i { "Screenshot saved to: ${screenshotFile.absolutePath}" }
        assert(screenshotFile.exists()) { "Screenshot file was not created: ${screenshotFile.absolutePath}" }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun captureScreenshot(filename: String): File {
        val imageBitmap: ImageBitmap = composeTestRule.onRoot().captureToImage()
        val awtImage: BufferedImage = imageBitmap.toAwtImage()

        val outputDir = File("build/screenshots").apply {
            mkdirs()
        }
        logger.d { "Screenshots directory: ${outputDir.absolutePath}" }

        val timestamp: String = DateTimeFormatter.format(createLocalDateTime())
        val screenshotFile = File(outputDir, "${filename}_$timestamp.png")

        ImageIO.write(awtImage, "png", screenshotFile)
        logger.d { "Screenshot file created: ${screenshotFile.absolutePath}" }

        return screenshotFile
    }
}
