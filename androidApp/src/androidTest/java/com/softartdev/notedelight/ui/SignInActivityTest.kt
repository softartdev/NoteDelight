package com.softartdev.notedelight.ui


import android.text.SpannableStringBuilder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.data.SafeRepo
import com.softartdev.notedelight.ui.splash.SplashActivity
import com.softartdev.notedelight.util.EspressoIdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

@LargeTest
@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    private val password = "password"

    @Rule
    @JvmField
    var activityTestRule = object : ActivityTestRule<SplashActivity>(SplashActivity::class.java) {
        override fun beforeActivityLaunched() {
            val safeRepo by inject(SafeRepo::class.java)
            while (safeRepo.databaseState == SQLCipherUtils.State.DOES_NOT_EXIST) {
                val db = safeRepo.buildDatabaseInstanceIfNeed()
                val notes = runBlocking { db.noteDao().getNotes().first() }
                Timber.d("notes = %s", notes)
                Timber.d("databaseState = %s", safeRepo.databaseState.name)
            }
            safeRepo.encrypt(SpannableStringBuilder(password))
            safeRepo.closeDatabase()
            Timber.d("databaseState = %s", safeRepo.databaseState.name)
        }
    }

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

        passwordEditText.perform(scrollTo(), replaceText(password))

        signInButton.perform(scrollTo(), click())

        val messageTextView = onView(withId(R.id.text_message))
        messageTextView.check(matches(withText(R.string.label_empty_result)))
        messageTextView.check(matches(isDisplayed()))
    }
}
