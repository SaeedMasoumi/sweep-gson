package io.saeid.sweep.gson.unwrapper

import io.saeid.sweep.gson.SweepUnwrapper
import io.saeid.sweep.gson.USE_DEFAULT_UNWRAPPER
/**
 * Indicates that [SweepUnwrapper] must use [unwrapWith] method to find out the unwrapper names,
 * when the [SweepUnwrapper.value] is set to [USE_DEFAULT_UNWRAPPER] or [force] is true.
 */
interface DefaultUnwrapper {

    /**
     * @return true, if you want to unwrap every object, even if they're annotated with [SweepUnwrapper] or not.
     */
    fun force(): Boolean = false

    fun <T> unwrapWith(type: Class<T>): String?
}