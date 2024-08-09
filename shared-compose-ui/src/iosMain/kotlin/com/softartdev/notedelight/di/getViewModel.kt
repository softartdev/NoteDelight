package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.softartdev.notedelight.shared.base.KmmViewModel
import org.koin.core.Koin
import org.koin.core.context.KoinContext
import org.koin.mp.KoinPlatformTools

@Composable
actual inline fun <reified T : KmmViewModel> getViewModel(): T = remember {
    val defaultContext: KoinContext = KoinPlatformTools.defaultContext()
    val koin: Koin = defaultContext.get()
    return@remember koin.get(clazz = T::class)
}