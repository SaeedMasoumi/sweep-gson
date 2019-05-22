package io.saeid.sweep.gson.hook

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.mockk.every
import io.mockk.mockkStatic
import io.saeid.sweep.gson.*
import io.saeid.sweep.gson.SweepReflection.sweepWrapperValue
import io.saeid.sweep.gson.createDefaultWrapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HooksTests {

    private lateinit var gson: Gson
    private val mockChild = Child("sweep")
    private val mockProperties = Properties("1234")

    @Before
    fun setUp() {
        mockkStatic(SweepReflection::class)
        every { sweepWrapperValue<Child>(any()) } returns USE_DEFAULT_WRAPPER
    }

    @Test
    fun `verify adding an object to the root with different wrapping types`() {
        setUpGson(mockProperties)
        assertHooks("""{"properties":{"id":"1234"},"data":{"name":"sweep"}}""")

        every { sweepWrapperValue<Child>(any()) } returns "a.b"
        assertHooks("""{"properties":{"id":"1234"},"a":{"b":{"name":"sweep"}}}""")

        every { sweepWrapperValue<Child>(any()) } returns USE_CLASS_NAME_WRAPPER
        assertHooks("""{"properties":{"id":"1234"},"child":{"name":"sweep"}}""")
    }

    @Test
    fun `verify adding a primitive type to the root `() {
        setUpGson("1234")
        assertHooks("""{"properties":"1234","data":{"name":"sweep"}}""")

        every { sweepWrapperValue<Child>(any()) } returns "a.b"
        assertHooks("""{"properties":"1234","a":{"b":{"name":"sweep"}}}""")

        every { sweepWrapperValue<Child>(any()) } returns USE_CLASS_NAME_WRAPPER
        assertHooks("""{"properties":"1234","child":{"name":"sweep"}}""")
    }

    private fun setUpGson(rootValue: Any) {
        gson = GsonBuilder().withSweep {
            defaultWrapper = createDefaultWrapper("data")
            hooks = object : Hooks {
                override fun <T> addToRoot(value: T): Pair<String, Any>? {
                    return Pair("properties", rootValue)
                }
            }
        }.create()
    }

    private fun assertHooks(expected: String) {
        val json = gson.toJson(mockChild)
        Assert.assertEquals(expected, json)
    }
}