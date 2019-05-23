package io.saeid.sweep.gson.wrapper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.mockk.every
import io.mockk.mockkStatic
import io.saeid.sweep.gson.*
import io.saeid.sweep.gson.SweepReflection.sweepWrapperValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WrapperTests {

    private lateinit var gson: Gson
    private val mockChild = Child("sweep")
    private val mockNoAnnotation = NoAnnotation("sweep")

    @Before
    fun setUp() {
        gson = GsonBuilder().withSweep {
            defaultWrapper = createDefaultWrapper("request")
        }.create()

        mockkStatic(SweepReflection::class)
    }

    @Test
    fun `verify sweep will not break current behavior of Gson`() {
        assertSerialized("""{"name":"sweep"}""", mockNoAnnotation)
    }

    @Test
    fun `verify wrapping with USE_CLASS_NAME_WRAPPER`() {
        assertSerialized("""{"child":{"name":"sweep"}}""", mockChild, USE_CLASS_NAME_WRAPPER)
    }

    @Test
    fun `verify wrapping with USE_DEFAULT_WRAPPER`() {
        assertSerialized("""{"request":{"name":"sweep"}}""", mockChild, USE_DEFAULT_WRAPPER)
    }

    @Test
    fun `verify wrapping with one level depth`() {
        assertSerialized("""{"data":{"name":"sweep"}}""", mockChild, "data")
    }

    @Test
    fun `verify wrapping with two level depth`() {
        assertSerialized("""{"request":{"data":{"name":"sweep"}}}""", mockChild, "request.data")
    }

    @Test
    fun `verify wrapping with three level depth using default wrapper`() {
        gson = GsonBuilder().withSweep {
            defaultWrapper =
                createDefaultWrapper("request.$USE_CLASS_NAME_WRAPPER.reply")
        }.create()

        assertSerialized(
            """{"request":{"child":{"reply":{"name":"sweep"}}}}""", mockChild,
            USE_DEFAULT_WRAPPER
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `throw exception when defaultWrapper is not set`() {
        gson = GsonBuilder().withSweep().create()
        assertSerialized("empty", mockChild, USE_DEFAULT_WRAPPER)
    }

    @Test
    fun `verify nested wrapping`() {
        gson = GsonBuilder().withSweep {
            defaultWrapper = createDefaultWrapper(USE_CLASS_NAME_WRAPPER)
        }.create()
        assertSerialized(
            """{"root":{"name":"root","child":{"child":{"name":"sweep"}}}}""",
            Root("root", mockChild),
            USE_DEFAULT_WRAPPER
        )
    }

    private fun <T> assertSerialized(expected: String, value: T, sweepWrapperValue: String = "") {
        every { sweepWrapperValue<Child>(any()) } returns sweepWrapperValue

        val json = gson.toJson(value)
        assertEquals(expected, json)
    }
}