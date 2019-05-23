package io.saeid.sweep.gson.unwrapper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.saeid.sweep.gson.*
import io.saeid.sweep.gson.SweepReflection.sweepUnwrapperValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class UnwrapperTests {

    private lateinit var gson: Gson
    private val defaultUnwrapper = mockk<DefaultUnwrapper>()

    @get:Rule
    val thrown: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        gson = GsonBuilder().withSweep {
            this.defaultUnwrapper = this@UnwrapperTests.defaultUnwrapper
        }.create()
        mockkStatic(SweepReflection::class)
    }

    @Test
    fun `verify unwrapping when force mode is on and class has @SweepUnwrapper annotation`() {
        every { defaultUnwrapper.force() } returns true

        // when DefaultUnwrapper is not used
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns ""

        deserialized<Child>("""{ "root" : { "name": "child" } }""", "root").apply {
            assertEquals("child", name)
        }
        deserialized<Child>("""{"data": {"root": {"name": "child" } } }""", "data.root").apply {
            assertEquals("child", name)
        }
        deserialized<Child>("""{ "child" : { "name": "child" } }""", USE_CLASS_NAME_UNWRAPPER).apply {
            assertEquals("child", name)
        }

        // when DefaultUnwrapper is used
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root"

        deserialized<Child>("""{ "root" : { "name": "child" } }""", USE_DEFAULT_UNWRAPPER).apply {
            assertEquals("child", name)
        }

        // when
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "data.root"
        // then
        deserialized<Child>("""{"data": {"root": {"name": "child" } } }""", USE_DEFAULT_UNWRAPPER).apply {
            assertEquals("child", name)
        }

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "data.$USE_CLASS_NAME_UNWRAPPER"
        deserialized<Child>("""{"data": {"child": {"name": "child" } } }""", USE_DEFAULT_UNWRAPPER).apply {
            assertEquals("child", name)
        }
    }

    @Test
    fun `verify unwrapping when force mode is on and class has no @SweepUnwrapper annotation`() {
        every { defaultUnwrapper.force() } returns true

        // when DefaultUnwrapper is used
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "root"

        deserialized<NoAnnotation>("""{ "root" : { "name": "child" } }""", "").apply {
            assertEquals("child", name)
        }

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "data.root"

        deserialized<NoAnnotation>("""{"data": {"root": {"name": "child" } } }""", "").apply {
            assertEquals("child", name)
        }

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "data.$USE_CLASS_NAME_UNWRAPPER"
        deserialized<NoAnnotation>("""{"data": {"noAnnotation": {"name": "child" } } }""", "").apply {
            assertEquals("child", name)
        }
    }

    @Test
    fun `throw exception when force mode is on and class without @SweepUnwrapper didn't provide DefaultUnwrapper`() {
        thrown.expectMessage("default unwrapper is forced but nothing provided. Try to implement unwrapWith() method or annotate your class with SweepUnwrapper")

        every { defaultUnwrapper.force() } returns true
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns null

        deserialized<NoAnnotation>("""{ "root" : { "name": "child" } }""", "root").apply {
            assertEquals("child", name)
        }
    }

    @Test
    fun `verify unwrapping when force mode is off`() {
        every { defaultUnwrapper.force() } returns false
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns ""

        deserialized<Child>("""{ "root" : { "name": "child" } }""", "root").apply {
            assertEquals("child", name)
        }

        deserialized<Child>("""{"data": {"root": {"name": "child" } } }""", "data.root").apply {
            assertEquals("child", name)
        }

        deserialized<Child>("""{"data": {"child": {"name": "child" } } }""", "data.$USE_CLASS_NAME_UNWRAPPER").apply {
            assertEquals("child", name)
        }

        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "data.$USE_CLASS_NAME_UNWRAPPER"

        deserialized<Child>("""{"data": {"child": {"name": "child" } } }""", USE_DEFAULT_UNWRAPPER).apply {
            assertEquals("child", name)
        }
    }

    @Test
    fun `verify sweep will not break current behavior of Gson`() {
        every { defaultUnwrapper.force() } returns true
        every { defaultUnwrapper.unwrapWith<Any>(any()) } returns "foo.bar"

        deserialized<NoAnnotation>("""{ "name": "child" }""", "foo.bar").apply {
            assertEquals("child", name)
        }
    }

    private inline fun <reified T> deserialized(json: String, sweepUnwrapperValue: String): T {
        every { sweepUnwrapperValue<T>(any()) } returns sweepUnwrapperValue
        return gson.fromJson(json, T::class.java)
    }
}