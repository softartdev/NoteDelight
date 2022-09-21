@file:OptIn(ExperimentalCoroutinesApi::class)

package com.softartdev.notedelight.old.ui

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.rule.ActivityTestRule
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.testIn
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.ui.splash.SplashActivity
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import leakcanary.DetectLeaksAfterTestSuccess
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

    @get:Rule
    val detectLeaksRule = DetectLeaksAfterTestSuccess()

    private val noteUseCase: NoteUseCase by KoinJavaComponent.inject(NoteUseCase::class.java)

    @Test
    fun prepopulateDatabase() = runTest {
        val turbine: ReceiveTurbine<List<Note>> = noteUseCase.getNotes().testIn(scope = this)

        var notes: List<Note> = turbine.awaitItem()
        assertEquals(0, notes.size)

        val stringBuilder = StringBuilder()
        for (num in 1..250) {
            noteUseCase.createNote(
                title = "Title #$num",
                text = stringBuilder.append("Lorem ipsum dolor sit amet. ").toString()
            )
            Espresso.onIdle()
            onView(withId(R.id.notes_recycler_view)).perform(scrollToPosition<ViewHolder>(0))

            notes = turbine.awaitItem()
            assertEquals(num, notes.size)
        }
        turbine.cancel()
    }

}
