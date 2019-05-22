package io.saeid.sweep.gson

import io.saeid.sweep.gson.wrapper.DefaultWrapper


/**
 * This constant is reserved word for [SweepWrapper] annotation.
 *
 * It will make [SweepWrapper] to use class name (decapitalized version) as the wrapper name.
 */
const val USE_CLASS_NAME_WRAPPER = "#c"

/**
 * This constant is reserved word for [SweepWrapper] annotation.
 *
 * It will force [SweepWrapper] to use [DefaultWrapper] to detect wrapper name.
 */
const val USE_DEFAULT_WRAPPER = "#d"

const val USE_CLASS_NAME_UNWRAPPER = "#c"