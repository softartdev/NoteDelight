package com.softartdev.notedelight.ui


import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.splash.SplashActivity
import com.softartdev.notedelight.shared.base.IdlingResource as EspressoIdlingResource
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditTitleTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun editTitleTest() {
        val floatingActionButton = onView(withId(R.id.add_note_fab))
        floatingActionButton.perform(click())

        val textInputEditText = onView(withId(R.id.note_edit_text))
        val text = "text"
        textInputEditText.perform(replaceText(text), closeSoftKeyboard())

        val saveNoteActionMenu = onView(withId(R.id.action_save_note))
        saveNoteActionMenu.perform(click())

        val navBackButton = onView(childAtPosition(
                parentMatcher = allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                                parentMatcher = withId(R.id.action_bar_container),
                                position = 0)),
                position = 1))
        navBackButton.perform(click())

        val textView = onView(withId(R.id.item_note_title_text_view))
        textView.check(matches(withText(text)))

        val cardView = onView(allOf(
                withId(R.id.item_note_card_view),
                childAtPosition(
                        parentMatcher = withId(R.id.notes_recycler_view),
                        position = 0)))
        cardView.perform(click())

        val actionBar = onView(childAtPosition(
                parentMatcher = withId(R.id.action_bar),
                position = 0))
        actionBar.check(matches(withText(text)))

        val actionMenuItemView = withMenuIdOrText(R.id.action_edit_title, R.string.action_edit_title)
        onView(actionMenuItemView).perform(click())

        val textInputEditTitle = onView(withId(R.id.edit_title_text_input))
        val title = "title"
        textInputEditTitle.perform(replaceText(title), closeSoftKeyboard())

        val okButton = onView(withId(android.R.id.button1))
        okButton.perform(click())

        actionBar.check(matches(withText(title)))

        navBackButton.perform(click())

        textView.check(matches(withText(title)))
    }

    @Suppress("SameParameterValue")
    private fun withMenuIdOrText(@IdRes id: Int, @StringRes menuText: Int): Matcher<View> {
        val matcher = withId(id)
        return try {
            onView(matcher).check(matches(isDisplayed()))
            matcher
        } catch (NoMatchingViewException: Exception) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            openActionBarOverflowOrOptionsMenu(context)
            withText(menuText)
        }
    }

}
