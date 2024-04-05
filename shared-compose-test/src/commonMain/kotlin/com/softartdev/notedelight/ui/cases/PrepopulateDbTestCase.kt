@file:OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import app.cash.turbine.test
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.ui.BaseTestCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.koin.mp.KoinPlatformTools
import kotlin.test.DefaultAsserter
import kotlin.time.Duration.Companion.minutes

class PrepopulateDbTestCase(
    composeTestRule: ComposeUiTest
) : () -> TestResult, BaseTestCase(composeTestRule) {

    private val noteDAO: NoteDAO = KoinPlatformTools.defaultContext().get().get()
    private val createNoteUseCase: CreateNoteUseCase = KoinPlatformTools.defaultContext().get().get()

    override fun invoke(): TestResult = runTest(timeout = 1.minutes) {
        noteDAO.listFlow.test {
            var notes: List<Note> = awaitItem()
            DefaultAsserter.assertEquals(expected = 0, actual = notes.size, message = null)

            for (num in 1..250) {
                val loremIpsum = LOREM_IPSUM.repeat(num)
                createNoteUseCase(title = "Title #$num", text = loremIpsum)
                composeTestRule.awaitIdle()

                notes = awaitItem()
                DefaultAsserter.assertEquals(expected = num, actual = notes.size, message = null)
            }
        }
    }

    companion object {
        private const val LOREM_IPSUM: String = "Lorem ipsum dolor sit amet. "
    }
}