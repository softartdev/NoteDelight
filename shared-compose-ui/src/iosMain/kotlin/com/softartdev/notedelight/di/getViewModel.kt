package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.core.Koin
import org.koin.core.context.KoinContext
import org.koin.mp.KoinPlatformTools

@Composable
actual inline fun <reified T : ViewModel> getViewModel(): T = remember {
    val defaultContext: KoinContext = KoinPlatformTools.defaultContext()
    val koin: Koin = defaultContext.get()
    return@remember koin.get(clazz = T::class)
}