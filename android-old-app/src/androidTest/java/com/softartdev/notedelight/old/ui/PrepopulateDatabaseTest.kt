@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.old.ui

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.rule.ActivityTestRule
import app.cash.turbine.test
import com.softartdev.notedelight.old.ui.splash.SplashActivity
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent

@FlakyTest
@RunWith(AndroidJUnit4::class)
class PrepopulateDatabaseTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    private val noteUseCase: NoteUseCase by KoinJavaComponent.inject(NoteUseCase::class.java)

    @Test
    fun prepopulateDatabase() = runTest {
        noteUseCase.getNotes().test {
            var notes: List<Note> = awaitItem()
            assertEquals(0, notes.size)

            val expectedSize: Int = populateDatabase()

            notes = expectMostRecentItem()
            assertEquals(expectedSize, notes.size)
        }
    }

    private suspend fun populateDatabase(size: Int = 250): Int {
        val stringBuilder = StringBuilder()
        repeat(times = size) { num: Int ->
            val loremIpsum = stringBuilder
                .append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ")
                .toString()
            noteUseCase.createNote(title = "Title #$num", text = loremIpsum)
            Espresso.onIdle()
        }
        return size
    }
}