package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.shared.base.KmmViewModel

@Composable
expect inline fun <reified T : KmmViewModel> getViewModel(): T