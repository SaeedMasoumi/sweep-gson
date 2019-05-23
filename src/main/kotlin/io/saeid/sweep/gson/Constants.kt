package io.saeid.sweep.gson

import io.saeid.sweep.gson.unwrapper.DefaultUnwrapper
import io.saeid.sweep.gson.wrapper.DefaultWrapper

/**
 * This constant is a reserved word for [SweepWrapper] annotation.
 *
 * It will force [SweepWrapper] to use [DefaultWrapper] to detect wrapper name.
 */
const val USE_DEFAULT_WRAPPER = "#d"

/**
 * This constant is a reserved word for [SweepWrapper] annotation.
 *
 * It will force [SweepWrapper] to use class name (decapitalized version) as the wrapper name.
 */
const val USE_CLASS_NAME_WRAPPER = "#c"

/**
 * This constant is a reserved word for [SweepUnwrapper] annotation.
 *
 * It will force [SweepUnwrapper] to use [DefaultUnwrapper] to detect unwrapper name.
 */
const val USE_DEFAULT_UNWRAPPER = "#d"

/**
 * This constant is a reserved word for [SweepUnwrapper] annotation.
 *
 * It will force [SweepWrapper] to use class name (decapitalized version) as the unwrapper name.
 */
const val USE_CLASS_NAME_UNWRAPPER = "#c"