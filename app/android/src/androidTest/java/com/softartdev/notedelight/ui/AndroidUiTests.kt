package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.di.backupTestModule
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

@LargeTest
@FlakyTest
@RunWith(AndroidJUnit4::class)
class AndroidUiTests : AbstractJvmUiTests() {

    override val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    @Before
    override fun setUp() = super.setUp()

    @After
    override fun tearDown() = super.tearDown()

    @Test
    override fun crudNoteTest() = super.crudNoteTest()

    @Test
    override fun createNoteWhileSelectedTest() = super.createNoteWhileSelectedTest()

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

    @Test
    override fun localeTest() = super.localeTest()

    @Test
    override fun backupFeatureTest() {
        loadKoinModules(backupTestModule)
        super.backupFeatureTest()
        unloadKoinModules(backupTestModule)
    }

    override fun pressBack() = Espresso.pressBack()

    override fun closeSoftKeyboard() = Espresso.closeSoftKeyboard()
}
