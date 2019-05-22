package io.saeid.sweep.gson.unwrapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.saeid.sweep.gson.Child
import io.saeid.sweep.gson.SweepReflection
import io.saeid.sweep.gson.USE_CLASS_NAME_UNWRAPPER
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UnwrapperNamesBuilderTests {

    private lateinit var builder: UnwrapperNamesBuilder
    private val defaultUnwrapper = mockk<DefaultUnwrapper>()

    @Before
    fun setUp() {
        builder = UnwrapperNamesBuilder(defaultUnwrapper)
        every { defaultUnwrapper.find<Child>(any()) } returns "root"
        mockkStatic(SweepReflection::class)
    }

    @Test
    fun `verify builder select annotation value when default wrapper is forced but class is marked with sweep annotation `() {
        assertBuilder(listOf("annotation"), "annotation")
        assertBuilder(listOf("a", "b"), "a.b")
        assertBuilder(listOf("a", "child"), "a.$USE_CLASS_NAME_UNWRAPPER")
        assertBuilder(listOf("a", "child", "b"), "a.$USE_CLASS_NAME_UNWRAPPER.b")
    }

    private fun assertBuilder(
        expected: List<String>,
        sweepUnwrapperValue: String,
        value: Any = Child("sweep"),
        force: Boolean = true
    ) {
        every { defaultUnwrapper.force() } returns force
        every { SweepReflection.sweepUnwrapperValue<Child>(any()) } returns sweepUnwrapperValue
        val actual = builder.build(value.javaClass)
        assertEquals(expected, actual)
    }
}