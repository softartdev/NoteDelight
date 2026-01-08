@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilDoesNotExist
import com.softartdev.notedelight.ASSERT_WAIT_TIMEOUT_MILLIS
import com.softartdev.notedelight.interactor.LocaleInteractor
import com.softartdev.notedelight.model.LanguageEnum
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitUntilSelected
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.choose_language
import org.jetbrains.compose.resources.getString
import org.koin.mp.KoinPlatform
import kotlin.test.assertEquals

class LocaleTestCase(
    composeUiTest: ComposeUiTest,
    private val pressBack: () -> Unit
) : () -> TestResult, BaseTestCase(composeUiTest) {

    override fun invoke() = runTest {
        val localeInteractor: LocaleInteractor = KoinPlatform.getKoin().get()
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
            settingsTestScreen {
                composeUiTest.waitUntilDisplayed("language", blockSNI = ::languageSNI)
                LanguageEnum.entries.forEach { languageEnum ->
                    languageSNI.performClick()
                    languageDialog {
                        composeUiTest.waitUntilDisplayed("langDialogTitle", ::langDialogTitleSNI)
                        languageEnum.radioButtonSNI.performClick()
                        composeUiTest.waitUntilSelected("radioButton") { languageEnum.radioButtonSNI }
                        confirmDialogButtonSNI.performClick()
                    }
                    composeUiTest.waitUntilDoesNotExist(
                        matcher = hasText(getString(Res.string.choose_language)),
                        timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
                    )
                    assertEquals(languageEnum, localeInteractor.languageEnum)
                }
                pressBack()
            }
        }
    }
}
