@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import com.softartdev.notedelight.ASSERT_WAIT_TIMEOUT_MILLIS
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitUntilSelected
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.choose_language
import org.jetbrains.compose.resources.getString
import org.koin.java.KoinJavaComponent

class LocaleTestCase(
    composeTestRule: ComposeContentTestRule,
    private val pressBack: () -> Unit
) : () -> Unit, BaseTestCase(composeTestRule) {

    override fun invoke() = runTest {
        val localeInteractor: LocaleInteractor = KoinJavaComponent.get(LocaleInteractor::class.java)
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                composeTestRule.waitUntilDisplayed(blockSNI = ::languageSNI)
                LanguageEnum.entries.forEach { languageEnum ->
                    languageSNI.performClick()
                    languageDialog {
                        composeTestRule.waitUntilDisplayed(::langDialogTitleSNI)
                        languageEnum.radioButtonSNI.performClick()
                        composeTestRule.waitUntilSelected { languageEnum.radioButtonSNI }
                        yesDialogButtonSNI.performClick()
                    }
                    composeTestRule.waitUntilDoesNotExist(
                        matcher = hasText(runBlocking { getString(Res.string.choose_language) }),
                        timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
                    )
                    assertEquals(languageEnum, localeInteractor.languageEnum)
                }
                pressBack()
            }
        }
    }
}
