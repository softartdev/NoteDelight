package com.softartdev.notedelight.util

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito

@Ignore("Separate process for LeakCanary currently disabled")
class ProcessCheckerTest {

    @Test
    fun `obtain by reflection`() {
        val mockContext: Context = Mockito.mock(Context::class.java)
        assertEquals(mockContext.isInLeakCanaryAnalyzerProcess, false)
    }
}