package io.saeid.sweep.gson.wrapper

import io.saeid.sweep.gson.SweepWrapper
import io.saeid.sweep.gson.USE_DEFAULT_WRAPPER

/**
 * Indicates that [SweepWrapper] must use [wrapWith] method to find out the wrapper names,
 * when the [SweepWrapper.value] is set to [USE_DEFAULT_WRAPPER].
 */
interface DefaultWrapper {
    fun <T> wrapWith(value: T): String?
}