package com.softartdev.notedelight.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.ui.splash.SplashActivity
import com.softartdev.notedelight.util.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CreateRemoveNoteWithUseCaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    private val noteUseCase by inject(NoteUseCase::class.java)

    private val title = "Test title"
    private val text = "Test text"

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun createRemoveNote() = runBlocking<Unit> {
        val messageTextView = onView(withId(R.id.text_message))
        messageTextView.check(matches(withText(R.string.label_empty_result)))

        var actList = noteUseCase.getNotes().first()
        var expList = emptyList<Note>()
        assertEquals(expList, actList)

        val expId = 1L
        val actId = noteUseCase.createNote(title, text)
        assertEquals(expId, actId)

        val actPair = noteUseCase.getNotes().first().single().let { it.title to it.text }
        val expPair = title to text
        assertEquals(expPair, actPair)

        messageTextView.check(matches(withEffectiveVisibility(Visibility.GONE)))

        val noteTitleTextView = onView(withId(R.id.item_note_title_text_view))
        noteTitleTextView.check(matches(withText(title)))

        val actDeleted = noteUseCase.deleteNote(1)
        val expDeleted = 1
        assertEquals(expDeleted, actDeleted)

        actList = noteUseCase.getNotes().first()
        expList = emptyList()
        assertEquals(expList, actList)

        messageTextView.check(matches(isDisplayed()))
    }

}