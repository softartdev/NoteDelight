package com.softartdev.notedelight.di

import com.softartdev.notedelight.shared.base.KmmViewModel
import org.koin.java.KoinJavaComponent.get

actual inline fun <reified T : KmmViewModel> getViewModel(): T = get(T::class.java)