package io.saeid.sweep.gson

import com.google.gson.GsonBuilder

/**
 * The entry point for using *Sweep Gson*.
 *
 * An extension function on [GsonBuilder] which adds (un)wrapping functionality.
 *
 * By default, All DefaultWrapper, DefaultUnwrapper and Hooks are disabled.
 *
 * Here is an examples of how this extension works:
 *
 * ```
 * GsonBuilder().withSweep {
 *
 *   defaultWrapper = //...
 *
 *   defaultUnwrapper = //...
 *
 *   hooks = //...
 *
 * }.create()
 * ```
 */
fun GsonBuilder.withSweep(builder: (Builder.() -> Unit)? = null): GsonBuilder {
    return Builder(this).also { builder?.invoke(it) }.build()
}