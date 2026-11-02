@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder
import com.softartdev.notedelight.repository.PreferencesRepo
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.usecase.note.NoteSortUseCase
import com.softartdev.notedelight.util.DateTimeFormatter
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortNotesTestCase(
    composeUiTest: ComposeUiTest,
) : () -> TestResult, BaseTestCase(composeUiTest) {

    private val noteDAO: NoteDAO by KoinPlatform.getKoin().inject()
    private val preferencesRepo: PreferencesRepo by KoinPlatform.getKoin().inject()
    private val noteSortUseCase: NoteSortUseCase by KoinPlatform.getKoin().inject()

    private val PreferencesRepo.noteSort: NoteSort
        get() = NoteSort(sortField, sortOrder)

    override fun invoke() = runTest {
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("sortMenuButton", blockSNI = ::sortMenuButtonSNI)
            noteSortUseCase(NoteSort())
            noteDAO.deleteAll()
            noteDAO.insert(BANANA_NOTE)
            noteDAO.insert(APPLE_NOTE)
            noteDAO.insert(CHERRY_NOTE)

            // Default: dateModified DESC -> banana (Feb 3) < Cherry (Feb 2) < apple (Jan 2)
            assertNotesOrder(
                firstTitle = BANANA_NOTE.title,
                secondTitle = CHERRY_NOTE.title,
                thirdTitle = APPLE_NOTE.title,
            )
            val bananaCreatedText = DateTimeFormatter.format(BANANA_NOTE.dateCreated)
            val bananaModifiedText = DateTimeFormatter.format(BANANA_NOTE.dateModified)
            val appleDateText = DateTimeFormatter.format(APPLE_NOTE.dateCreated)
            composeUiTest.waitUntilDisplayed("banana created date") {
                noteDateSNI(bananaCreatedText)
            }
            composeUiTest.waitUntilDisplayed("banana modified date") {
                noteDateSNI(bananaModifiedText)
            }
            // APPLE_NOTE has dateCreated == dateModified -> rendered once without duplicate timestamp
            composeUiTest.waitUntilDisplayed("apple single date") {
                noteDateSNI(appleDateText)
            }
            noteDatesSNIC(appleDateText).assertCountEquals(1)

            // title ASC (COLLATE NOCASE) -> apple < banana < Cherry
            val titleAsc = NoteSort(field = SortField.TITLE, order = SortOrder.ASC)
            selectSortOption(titleAsc)
            assertNotesOrder(
                firstTitle = APPLE_NOTE.title,
                secondTitle = BANANA_NOTE.title,
                thirdTitle = CHERRY_NOTE.title,
            )
            assertEquals(titleAsc, preferencesRepo.noteSort)

            // title DESC (COLLATE NOCASE) -> Cherry < banana < apple
            val titleDesc = NoteSort(field = SortField.TITLE, order = SortOrder.DESC)
            selectSortOption(titleDesc)
            assertNotesOrder(
                firstTitle = CHERRY_NOTE.title,
                secondTitle = BANANA_NOTE.title,
                thirdTitle = APPLE_NOTE.title,
            )
            assertEquals(titleDesc, preferencesRepo.noteSort)

            // dateCreated ASC -> banana (Jan 1) < apple (Jan 2) < Cherry (Jan 3)
            val createdAsc = NoteSort(field = SortField.DATE_CREATED, order = SortOrder.ASC)
            selectSortOption(createdAsc)
            assertNotesOrder(
                firstTitle = BANANA_NOTE.title,
                secondTitle = APPLE_NOTE.title,
                thirdTitle = CHERRY_NOTE.title,
            )
            composeUiTest.waitUntilDisplayed("banana created date") {
                noteDateSNI(bananaCreatedText)
            }
            composeUiTest.waitUntilDisplayed("banana modified date") {
                noteDateSNI(bananaModifiedText)
            }
            noteDatesSNIC(appleDateText).assertCountEquals(1)
            assertEquals(createdAsc, preferencesRepo.noteSort)

            noteSortUseCase(NoteSort())
        }
    }

    private fun MainTestScreen.selectSortOption(noteSort: NoteSort) {
        sortMenuButtonSNI.performClick()
        composeUiTest.waitUntilDisplayed("sortOption $noteSort") {
            sortOptionSNI(noteSort)
        }
        sortOptionSNI(noteSort).performClick()
    }

    private fun MainTestScreen.assertNotesOrder(
        firstTitle: String,
        secondTitle: String,
        thirdTitle: String,
    ) {
        composeUiTest.waitAssert("Notes order: $firstTitle < $secondTitle < $thirdTitle") {
            val firstTop = noteItemSNI(firstTitle)
                .assertIsDisplayed()
                .fetchSemanticsNode()
                .boundsInRoot.top
            val secondTop = noteItemSNI(secondTitle)
                .assertIsDisplayed()
                .fetchSemanticsNode()
                .boundsInRoot.top
            val thirdTop = noteItemSNI(thirdTitle)
                .assertIsDisplayed()
                .fetchSemanticsNode()
                .boundsInRoot.top
            assertTrue(
                actual = firstTop < secondTop && secondTop < thirdTop,
                message = "Expected $firstTitle ($firstTop) < $secondTitle ($secondTop) < $thirdTitle ($thirdTop)",
            )
        }
    }

    private companion object {
        val BANANA_NOTE = Note(
            id = 1L,
            title = "banana",
            text = "banana text",
            dateCreated = LocalDateTime(2024, 1, 1, 10, 0),
            dateModified = LocalDateTime(2024, 2, 3, 13, 0),
        )
        val APPLE_NOTE = Note(
            id = 2L,
            title = "apple",
            text = "apple text",
            dateCreated = LocalDateTime(2024, 1, 2, 11, 0),
            dateModified = LocalDateTime(2024, 1, 2, 11, 0),
        )
        val CHERRY_NOTE = Note(
            id = 3L,
            title = "Cherry",
            text = "cherry text",
            dateCreated = LocalDateTime(2024, 1, 3, 14, 0),
            dateModified = LocalDateTime(2024, 2, 2, 12, 0),
        )
    }
}
