package com.softartdev.notedelight.di

import com.softartdev.notedelight.shared.base.KmmViewModel
import org.koin.core.Koin
import org.koin.core.context.KoinContext
import org.koin.mp.KoinPlatformTools

actual inline fun <reified T : KmmViewModel> getViewModel(): T {
    val defaultContext: KoinContext = KoinPlatformTools.defaultContext()
    val koin: Koin = defaultContext.get()
    return koin.get(clazz = T::class)
}