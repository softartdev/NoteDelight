@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import app.cash.turbine.test
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.koin.mp.KoinPlatform
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class PrepopulateDbTestCase(
    composeUiTest: ComposeUiTest
) : () -> TestResult, BaseTestCase(composeUiTest) {

    private val noteDAO: NoteDAO by KoinPlatform.getKoin().inject()
    private val createNoteUseCase: CreateNoteUseCase by KoinPlatform.getKoin().inject()

    override fun invoke() = runTest(timeout = 3.minutes) {
        noteDAO.listFlow.test {
            var notes: List<Note> = awaitItem()
            assertEquals(0, notes.size)

            for (num in 1..250) {
                val loremIpsum = LOREM_IPSUM.repeat(num)
                createNoteUseCase(title = "Title #$num", text = loremIpsum)
                composeUiTest.awaitIdle()

                notes = awaitItem()
                assertEquals(num, notes.size)
            }
        }
    }

    companion object {
        private const val LOREM_IPSUM: String = "Lorem ipsum dolor sit amet. "
    }
}