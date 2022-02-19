package com.softartdev.notedelight.shared.util

import android.content.Context
import org.junit.Assert.*

import org.junit.Test
import org.mockito.Mockito

class ProcessCheckerTest {

    @Test
    fun `obtain by reflection`() {
        val mockContext: Context = Mockito.mock(Context::class.java)
        assertEquals(mockContext.isInLeakCanaryAnalyzerProcess, false)
    }
}