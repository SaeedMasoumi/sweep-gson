package io.saeid.sweep.gson.unwrapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.saeid.sweep.gson.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class UnwrapperNamesBuilderTests {

    private lateinit var builder: UnwrapperNamesBuilder
    private val defaultUnwrapper = mockk<DefaultUnwrapper>()

    @get:Rule
    val thrown: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        builder = UnwrapperNamesBuilder(defaultUnwrapper)
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root"
        mockkStatic(SweepReflection::class)
    }

    @Test
    fun `verify builder uses @SeepUnwrapper`() {
        val mockedChild = Child("sweep")

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns null

        assertBuilder(listOf("root"), mockedChild, "root")
        assertBuilder(listOf("foo", "bar"), mockedChild, "foo.bar")
        assertBuilder(listOf("foo", "child"), mockedChild, "foo.$USE_CLASS_NAME_UNWRAPPER")
    }

    @Test
    fun `verify builder uses @SeepUnwrapper with DefaultUnwrapper`() {
        val mockedChild = Child("sweep")

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root"
        assertBuilder(listOf("foo", "root"), mockedChild, "foo.$USE_DEFAULT_UNWRAPPER")

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root.$USE_CLASS_NAME_UNWRAPPER"
        assertBuilder(listOf("bar", "root", "child"), mockedChild, "bar.$USE_DEFAULT_UNWRAPPER")

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root"
        assertBuilder(
            listOf("bar", "root", "child"),
            mockedChild,
            "bar.$USE_DEFAULT_UNWRAPPER.$USE_CLASS_NAME_UNWRAPPER"
        )
    }

    @Test
    fun `verify builder selects annotation value when default wrapper is forced and class has no sweep annotation`() {
        val mockedChild = NoAnnotation("sweep")

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root"
        assertBuilder(listOf("root"), mockedChild, "")
    }


    @Test
    fun `throw exception when DefaultUnwrapper returns null`() {
        thrown.expectMessage("class io.saeid.sweep.gson.Child forced to use default unwrapper, but nothing provided. Try to implement DefaultUnWrapper")
        val mockedChild = Child("sweep")

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns null

        assertBuilder(emptyList(), mockedChild, USE_DEFAULT_UNWRAPPER)
    }

    private fun assertBuilder(expected: List<String>, value: Any, sweepUnwrapperValue: String) {
        every { SweepReflection.sweepUnwrapperValue<Child>(any()) } returns sweepUnwrapperValue
        val actual = builder.build(value.javaClass)
        assertEquals(expected, actual)
    }
}