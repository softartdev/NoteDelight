package com.softartdev.notedelight.util

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class ProcessCheckerTest {

    @Test
    fun `obtain by reflection`() {
        val mockContext: Context = Mockito.mock(Context::class.java)
        assertEquals(mockContext.isInLeakCanaryAnalyzerProcess, false)
    }
}