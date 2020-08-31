package com.softartdev.notedelight.ui


import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.data.SafeRepo
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.ui.splash.SplashActivity
import com.softartdev.notedelight.util.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
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
class CreateRemoveNoteWithDaoTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    private val safeRepo by inject(SafeRepo::class.java)
    private val noteDao = safeRepo.noteDao

    private val note = Note(
            id = 0,
            title = "Test title",
            text = "Test text",
            dateCreated = createLocalDateTime(),
            dateModified = createLocalDateTime()
    )

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

        var act = noteDao.getNotes().first()
        var exp = emptyList<Note>()
        assertEquals(exp, act)

        val expId = note.id + 1
        val actId = noteDao.insertNote(note)
        assertEquals(expId, actId)

        act = noteDao.getNotes().first()
        exp = listOf(note.copy(id = expId))
        assertEquals(exp, act)

        val swipeRefresh = onView(withId(R.id.main_swipe_refresh))
        swipeRefresh.perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)))

        messageTextView.check(matches(withEffectiveVisibility(Visibility.GONE)))

        val noteTitleTextView = onView(withId(R.id.item_note_title_text_view))
        noteTitleTextView.check(matches(withText(note.title)))

        val actDeleted = noteDao.deleteNoteById(expId)
        val expDeleted = 1
        assertEquals(expDeleted, actDeleted)

        act = noteDao.getNotes().first()
        exp = emptyList()
        assertEquals(exp, act)

        swipeRefresh.perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)))

        messageTextView.check(matches(isDisplayed()))
    }

    private fun withCustomConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = constraints
        override fun getDescription(): String = action.description
        override fun perform(uiController: UiController?, view: View?) = action.perform(uiController, view)
    }
}
