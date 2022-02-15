package com.softartdev.notedelight

import android.content.Context
import leakcanary.LeakCanaryProcess


val Context.isInLeakCanaryAnalyzerProcess: Boolean
    get() = LeakCanaryProcess.isInAnalyzerProcess(this)