package io.saeid.sweep.gson.wrapper

import io.saeid.sweep.gson.SweepWrapper
import io.saeid.sweep.gson.USE_DEFAULT_WRAPPER

/**
 * Finds a default wrapper name for a given class.
 * SweepGson will automatically switch to the default wrapper,
 * When [SweepWrapper.value] is [USE_DEFAULT_WRAPPER].
 */
interface DefaultWrapper {
    fun <T> wrapWith(value: T): String?
}