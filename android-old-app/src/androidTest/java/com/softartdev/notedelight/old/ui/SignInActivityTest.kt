package com.softartdev.notedelight.old.ui


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.ui.splash.SplashActivity
import com.softartdev.notedelight.shared.test.util.Encryptor
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.softartdev.notedelight.shared.base.IdlingResource as EspressoIdlingResource

@LargeTest
@RunWith(AndroidJUnit4::class)
class SignInActivityTest {

    @Rule
    @JvmField
    var activityTestRule = object : ActivityTestRule<SplashActivity>(SplashActivity::class.java) {
        override fun beforeActivityLaunched() = Encryptor.encryptDB()
    }

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
    fun signInActivityTest() {
        togglePasswordVisibility(R.id.password_text_input_layout)

        val passwordEditText = onView(withId(R.id.password_edit_text))
                .check(matches(isDisplayed()))
                .check(matches(withHint(R.string.enter_password)))

        passwordEditText.perform(replaceText("incorrect password"), closeSoftKeyboard())

        val signInButton = onView(withId(R.id.sign_in_button))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.sign_in)))
        signInButton.perform(scrollTo(), click())

        val passwordTextInputLayout = onView(withId(R.id.password_text_input_layout))
                .check(matches(isDisplayed()))
        passwordTextInputLayout.check(matches(withError(R.string.incorrect_password)))

        passwordEditText.perform(scrollTo(), replaceText(Encryptor.PASSWORD))

        signInButton.perform(scrollTo(), click())

        val messageTextView = onView(withId(R.id.text_message))
        messageTextView.check(matches(withText(R.string.label_empty_result)))
        messageTextView.check(matches(isDisplayed()))
    }
}
