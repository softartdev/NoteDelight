package com.softartdev.notedelight

import android.content.Context


val Context.isInLeakCanaryAnalyzerProcess: Boolean
    get() = false