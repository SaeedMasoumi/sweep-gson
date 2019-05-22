package io.saeid.sweep.gson.wrapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.saeid.sweep.gson.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WrapperNamesBuilderTests {

    private lateinit var builder: WrapperNamesBuilder
    private val defaultWrapper = mockk<DefaultWrapper>()

    @Before
    fun setUp() {
        builder = WrapperNamesBuilder(defaultWrapper)
        every { defaultWrapper.wrapWith<Child>(any()) } returns "root"
        mockkStatic(SweepReflection::class)
    }

    @Test
    fun `verify WrapperNamesBuilder on different conditions`() {
        assertBuilder(listOf("root"), USE_DEFAULT_WRAPPER)
        assertBuilder(listOf("child"), USE_CLASS_NAME_WRAPPER)
        assertBuilder(listOf("data"), "data")
        assertBuilder(listOf("data", "raw"), "data.raw")
        assertBuilder(listOf("data", "child"), "data.$USE_CLASS_NAME_WRAPPER")
        assertBuilder(listOf("data", "root"), "data.$USE_DEFAULT_WRAPPER")
        assertBuilder(listOf("a", "b", "c"), "a.b.c")
        assertBuilder(listOf("a", "child", "b"), "a.$USE_CLASS_NAME_WRAPPER.b")
        assertBuilder(listOf("a", "root", "b"), "a.$USE_DEFAULT_WRAPPER.b")
    }

    @Test(expected = IllegalStateException::class)
    fun `throw exception when value is not annotated with SweepWrapper`() {
        assertBuilder(
            listOf("root"),
            USE_DEFAULT_WRAPPER,
            NoAnnotation("someValue")
        )
    }

    private fun assertBuilder(
        expected: List<String>,
        sweepWrapperValue: String,
        value: Any = Child("sweep")
    ) {
        every { SweepReflection.sweepWrapperValue<Child>(any()) } returns sweepWrapperValue
        val actual = builder.build(value)
        assertEquals(expected, actual)
    }
}