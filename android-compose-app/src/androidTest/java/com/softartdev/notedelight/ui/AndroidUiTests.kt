package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.shared.base.IdlingRes
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AndroidUiTests : AbstractUiTests() {

    override val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    override fun setUp() {
        super.setUp()
        IdlingRegistry.getInstance().register(IdlingRes.countingIdlingResource)
    }

    override fun tearDown() {
        super.tearDown()
        IdlingRegistry.getInstance().unregister(IdlingRes.countingIdlingResource)
    }

    @Test
    override fun crudNoteTest() = super.crudNoteTest()

    @Test
    override fun editTitleAfterCreateTest() = super.editTitleAfterCreateTest()

    @Test
    override fun editTitleAfterSaveTest() = super.editTitleAfterSaveTest()

    @Test
    override fun prepopulateDatabase() = super.prepopulateDatabase()

    @Test
    override fun flowAfterCryptTest() = super.flowAfterCryptTest()

    @Test
    override fun settingPasswordTest() = super.settingPasswordTest()

    override fun pressBack() = Espresso.pressBack()

    override fun closeSoftKeyboard() = Espresso.closeSoftKeyboard()
}
