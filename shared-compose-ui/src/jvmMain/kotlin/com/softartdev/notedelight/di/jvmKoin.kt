package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.softartdev.notedelight.shared.base.KmmViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
actual inline fun <reified T : KmmViewModel> getViewModel(): T = remember { get(T::class.java) }