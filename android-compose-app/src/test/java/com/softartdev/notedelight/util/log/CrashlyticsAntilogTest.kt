package com.softartdev.notedelight.util.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Napier
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock

class CrashlyticsAntilogTest {

    private val mockCrashlytics = Mockito.mock(FirebaseCrashlytics::class.java)
    private val crashlyticsAntilog = CrashlyticsAntilog(mockCrashlytics)
    private val actualStringBuilder = StringBuilder()

    @Before
    fun setUp() {
        Mockito.`when`(
            mockCrashlytics.log(anyString())
        ).then { invocation: InvocationOnMock ->
            val message: String = invocation.getArgument(0) as String
            actualStringBuilder.appendLine(value = message)
        }
        Mockito.`when`(
            mockCrashlytics.recordException(any(Throwable::class.java))
        ).then { invocation: InvocationOnMock ->
            val throwable: Throwable? = invocation.getArgument(0) as Throwable?
            actualStringBuilder.appendLine(value = throwable?.stackTraceToString())
        }
        Napier.base(antilog = crashlyticsAntilog)
    }

    @After
    fun tearDown() {
        actualStringBuilder.clear()
        Napier.takeLogarithm()
    }

    @Test
    fun `perform VERBOSE log`() {
        Napier.v("")
        assertEquals("V/?: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform DEBUG log`() {
        Napier.d("")
        assertEquals("D/?: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform INFO log`() {
        Napier.i("")
        assertEquals("I/?: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform WARNING log`() {
        Napier.w("")
        assertEquals("W/?: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform ERROR log`() {
        Napier.e("")
        assertEquals("E/?: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform ASSERT log`() {
        Napier.wtf("")
        assertEquals("A/?: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform log with message`() {
        Napier.d("message")
        assertEquals("D/?: message\n", actualStringBuilder.toString())
    }

    @Test
    fun `perform log with tag`() {
        Napier.d("", tag = "TAG")
        assertEquals("D/TAG: \n", actualStringBuilder.toString())
    }

    @Test
    fun `perform log with tag & message`() {
        Napier.d("message", tag = "TAG")
        assertEquals("D/TAG: message\n", actualStringBuilder.toString())
    }

    @Test
    fun `perform log with throwable`() {
        val stubThrowable = Throwable(message = "stub")
        Napier.e("", throwable = stubThrowable)
        val expected = "E/?: \n${stubThrowable.stackTraceToString()}\n"
        assertEquals(expected, actualStringBuilder.toString())
    }

    @Test
    fun `perform log with throwable & tag & message`() {
        val stubThrowable = Throwable(message = "stub")
        Napier.e("message", throwable = stubThrowable, tag = "TAG")
        val expected = "E/TAG: message\n${stubThrowable.stackTraceToString()}\n"
        assertEquals(expected, actualStringBuilder.toString())
    }
}
