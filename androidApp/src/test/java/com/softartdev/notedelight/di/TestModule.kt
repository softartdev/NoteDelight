package com.softartdev.notedelight.di

import com.softartdev.notedelight.shared.data.CryptUseCase
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.SafeRepo
import com.softartdev.notedelight.util.PreferencesHelper
import org.koin.dsl.module
import org.mockito.Mockito.mock

val testModule = module {
    single { mock(PreferencesHelper::class.java) }
    single { mock(SafeRepo::class.java) }
    single { CryptUseCase(get()) }
    single { NoteUseCase(get()) }
}