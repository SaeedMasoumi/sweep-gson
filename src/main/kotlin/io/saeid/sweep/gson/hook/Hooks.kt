package io.saeid.sweep.gson.hook

import io.saeid.sweep.gson.SweepWrapper

/**
 * Enables some hooks during serialization.
 */
interface Hooks {

    /**
     * Adds given pair to the root of [value] object. Currently, It only available when [value] is annotated with [SweepWrapper].
     *
     * For example passing `Pair("prop","123")` for the below class
     *
     * ```
     * @SweepJson("data")
     * data class Foo(val name:String)
     * ```
     *
     * will converts the final json to:
     *
     * {
     *    "prop" : "123",
     *    "data" : {
     *         "name" : "string"
     *    }
     * }
     * ```
     * @return a pair containing a name and a value, null if you want to disable it.
     */
    fun <T> addToRoot(value: T): Pair<String, Any>? = null
}