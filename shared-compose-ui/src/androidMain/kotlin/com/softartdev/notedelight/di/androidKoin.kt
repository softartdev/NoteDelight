package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.shared.base.KmmViewModel
import org.koin.androidx.compose.getViewModel

@Composable
actual inline fun <reified T : KmmViewModel> getViewModel(): T = getViewModel()