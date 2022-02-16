package com.softartdev.notedelight.old.ui

import android.content.Context
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.ui.splash.SplashActivity
import leakcanary.DetectLeaksAfterTestSuccess
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.softartdev.notedelight.shared.base.IdlingResource as EspressoIdlingResource

@LargeTest
@RunWith(AndroidJUnit4::class)
class FlowAfterCryptTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @get:Rule
    val detectLeaksRule = DetectLeaksAfterTestSuccess()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val password = "password"

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun flowAfterCryptTest() {
        //main
        val floatingActionButton = onView(withId(R.id.add_note_fab))
        floatingActionButton.perform(click())
        //note
        val textInputEditText = onView(withId(R.id.note_edit_text))
        val titleText = "Lorem"
        textInputEditText.perform(replaceText(titleText))
        textInputEditText.perform(closeSoftKeyboard())

        openActionBarOverflowOrOptionsMenu(context)

        val settingsMaterialTextView = onView(Matchers.allOf(
                withId(R.id.title),
                withText(R.string.settings),
                isDisplayed()))
        settingsMaterialTextView.perform(click())

        //settings
        val switch = onView(Matchers.allOf(
                withId(android.R.id.switch_widget),
                childAtPosition(
                        parentMatcher = Matchers.allOf(
                                withId(android.R.id.widget_frame),
                                childAtPosition(
                                        parentMatcher = IsInstanceOf.instanceOf(LinearLayout::class.java),
                                        position = 2)
                        ),
                        position = 0),
                isDisplayed()))
        switch.check(matches(Matchers.not(isChecked())))
        switch.perform(click())

        val confirmPasswordEditText = onView(withId(R.id.set_password_edit_text))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))
        confirmPasswordEditText.check(matches(withHint(R.string.enter_password)))
        confirmPasswordEditText.perform(replaceText(password), closeSoftKeyboard())

        val confirmRepeatPasswordEditText = onView(withId(R.id.repeat_set_password_edit_text))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))
        confirmRepeatPasswordEditText.check(matches(withHint(R.string.confirm_password)))
        confirmRepeatPasswordEditText.perform(replaceText(password), closeSoftKeyboard())

        val confirmOkButton = onView(Matchers.allOf(
                withId(android.R.id.button1),
                withText(android.R.string.ok),
                childAtPosition(
                        parentMatcher = childAtPosition(
                                parentMatcher = withId(R.id.buttonPanel),
                                position = 0),
                        position = 3)
        ))
        confirmOkButton.perform(click())

        switch.check(matches(isChecked()))

        pressBack()
        //note
        pressBack()

        Thread.sleep(500)//FIXME
        val alertDialogPositiveButton = onView(withId(android.R.id.button1))
        alertDialogPositiveButton.perform(click())
        //main
        val textView = onView(withId(R.id.item_note_title_text_view))
        textView.check(matches(withText(titleText)))

        val settingsActionMenuItemView = onView(Matchers.allOf(
                withId(R.id.action_settings),
                withContentDescription(R.string.settings),
                isDisplayed()))
        settingsActionMenuItemView.perform(click())
        //settings
        switch.check(matches(isChecked()))
        switch.perform(click())

        val enterPasswordEditText = onView(withId(R.id.enter_password_edit_text))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))
        enterPasswordEditText.check(matches(withHint(R.string.enter_password)))
        enterPasswordEditText.perform(replaceText(password), closeSoftKeyboard())

        val enterOkButton = onView(Matchers.allOf(
                withId(android.R.id.button1),
                withText(android.R.string.ok),
                childAtPosition(
                        parentMatcher = childAtPosition(
                                parentMatcher = withId(R.id.buttonPanel),
                                position = 0),
                        position = 3)
        ))
        enterOkButton.perform(click())

        switch.check(matches(Matchers.not(isChecked())))

        pressBack()
        //main
        textView.perform(click())
        //note
        openActionBarOverflowOrOptionsMenu(context)
        val actionMenuItemView = onView(allOf(
                withId(R.id.title),
                withText(R.string.action_delete_note),
                isDisplayed()))
        actionMenuItemView.perform(click())

        alertDialogPositiveButton.perform(click())
        //main
        val textView2 = onView(withId(R.id.text_message))
        textView2.check(matches(withText(R.string.label_empty_result)))
        textView2.check(matches(isDisplayed()))
    }
}