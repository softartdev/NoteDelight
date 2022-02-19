package com.softartdev.notedelight.old.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.ui.splash.SplashActivity
import leakcanary.DetectLeaksAfterTestSuccess
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.softartdev.notedelight.shared.base.IdlingResource as EspressoIdlingResource

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreateRemoveNoteTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @get:Rule
    val detectLeaksRule = DetectLeaksAfterTestSuccess()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun createRemove() {
        val floatingActionButton = onView(withId(R.id.add_note_fab))
        floatingActionButton.perform(click())

        val textInputEditText = onView(withId(R.id.note_edit_text))
        val titleText = "Lorem"
        textInputEditText.perform(replaceText(titleText))
        textInputEditText.perform(closeSoftKeyboard())

        pressBack()

        val alertDialogPositiveButton = onView(withId(android.R.id.button1))
        alertDialogPositiveButton.perform(click())

        val textView = onView(withId(R.id.item_note_title_text_view))
        textView.check(matches(withText(titleText)))

        textView.perform(click())

        openActionBarOverflowOrOptionsMenu(context)

        val actionMenuItemView = onView(allOf(
                withId(R.id.title),
                withText(R.string.action_delete_note),
                isDisplayed()))
        actionMenuItemView.perform(click())

        alertDialogPositiveButton.perform(click())

        val textView2 = onView(withId(R.id.text_message))
        textView2.check(matches(withText(R.string.label_empty_result)))
    }

}
