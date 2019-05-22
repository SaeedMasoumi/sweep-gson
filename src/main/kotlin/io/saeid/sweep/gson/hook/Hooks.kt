package io.saeid.sweep.gson.hook

import io.saeid.sweep.gson.SweepWrapper

/**
 * Enables some hooks during serialization.
 */
interface Hooks {

    /**
     * Add given pair to the root of [value]. Currently, It only available when [value] is annotated with [SweepWrapper].
     *
     * @return a json object contains it's name and value, null if you want to disable it.
     */
    fun <T> addToRoot(value: T): Pair<String, Any>? = null
}