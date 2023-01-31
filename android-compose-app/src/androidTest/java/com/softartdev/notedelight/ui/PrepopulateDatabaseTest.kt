package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import app.cash.turbine.test
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.test.runTest
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent

@FlakyTest
@RunWith(AndroidJUnit4::class)
class PrepopulateDatabaseTest {

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val noteUseCase: NoteUseCase by KoinJavaComponent.inject(NoteUseCase::class.java)

    @Test
    fun prepopulateDatabase() = runTest {
        noteUseCase.getNotes().test {
            var notes: List<Note> = awaitItem()
            assertEquals(0, notes.size)

            for (num in 1..250) {
                val loremIpsum = LoremIpsum(words = num).values.joinToString()
                noteUseCase.createNote(title = "Title #$num", text = loremIpsum)
                composeTestRule.awaitIdle()

                notes = awaitItem()
                assertEquals(num, notes.size)
            }
        }
    }

}
