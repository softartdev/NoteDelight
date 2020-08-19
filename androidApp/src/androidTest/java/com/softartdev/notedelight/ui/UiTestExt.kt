package com.softartdev.notedelight.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.material.textfield.TextInputLayout
import com.softartdev.notedelight.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import timber.log.Timber


fun togglePasswordVisibility(textInputLayoutResId: Int) {
    val checkableImageButton = Espresso.onView(Matchers.allOf(
        ViewMatchers.withId(R.id.text_input_end_icon),
        ViewMatchers.isDescendantOfA(ViewMatchers.withId(textInputLayoutResId)),
        ViewMatchers.isDisplayed()))
    checkableImageButton.perform(ViewActions.click())
}

fun withError(
        expectedErrorTextResId: Int,
        context: Context = ApplicationProvider.getApplicationContext()
): Matcher<View> = withError(
        expectedErrorText = context.getString(expectedErrorTextResId)
)

fun withError(expectedErrorText: String): Matcher<View> = object : TypeSafeMatcher<View>() {
    override fun matchesSafely(item: View): Boolean = when (item) {
        is TextInputLayout -> {
            val actualErrorText = item.error.toString()
            Timber.d("Actual error text: $actualErrorText")
            expectedErrorText == actualErrorText
        }
        else -> false
    }

    override fun describeTo(description: Description) {
        description.appendText("Expected error text: $expectedErrorText")
    }
}

fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
    public override fun matchesSafely(view: View): Boolean {
        val parent = view.parent
        return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(position)
    }

    override fun describeTo(description: Description) {
        description.appendText("Child at position $position in parent ")
        parentMatcher.describeTo(description)
    }
}