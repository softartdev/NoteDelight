package com.softartdev.notedelight.shared.util

import android.content.Context


val Context.isInLeakCanaryAnalyzerProcess: Boolean
    get() = false