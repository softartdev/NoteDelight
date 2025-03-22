package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import app.cash.turbine.test
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.koin.java.KoinJavaComponent
import kotlin.time.Duration.Companion.minutes

class PrepopulateDbTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {

    private val noteDAO: NoteDAO by KoinJavaComponent.inject(NoteDAO::class.java)
    private val createNoteUseCase: CreateNoteUseCase by KoinJavaComponent.inject(CreateNoteUseCase::class.java)

    override fun invoke() = runTest(timeout = 1.minutes) {
        noteDAO.listFlow.test {
            var notes: List<Note> = awaitItem()
            Assert.assertEquals(0, notes.size)

            for (num in 1..250) {
                val loremIpsum = LOREM_IPSUM.repeat(num)
                createNoteUseCase(title = "Title #$num", text = loremIpsum)
                composeTestRule.awaitIdle()

                notes = awaitItem()
                Assert.assertEquals(num, notes.size)
            }
        }
    }

    companion object {
        private const val LOREM_IPSUM: String = "Lorem ipsum dolor sit amet. "
    }
}