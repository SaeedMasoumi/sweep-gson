package io.saeid.sweep.gson

import io.saeid.sweep.gson.wrapper.DefaultWrapper
import io.saeid.sweep.gson.unwrapper.DefaultUnwrapper

/**
 * An annotation that indicates this class should be wrapped with the provided name [value] during serialization.
 *
 * By default, It will use the value from [USE_DEFAULT_WRAPPER], which means it will detect the name from
 * [DefaultWrapper].
 *
 * This annotation also supports nested wrapping using dot as delimiter (e.g. "a.b.c").
 *
 * Here are some examples of how this annotation is meant to be used:
 *
 * ```
 * @SweepWrapper(USE_CLASS_NAME_WRAPPER)
 * data class Foo(val name: String)
 *
 * output:
 * { "foo" : { "name" : "string" } }
 *
 * @SweepWrapper("root.data")
 * data class Foo(val name: String)
 *
 * output:
 * { "root" : { "data" : { "name" : "string" } } }
 *
 *
 * @SweepWrapper // assume that default wrapper will return "root"
 * data class Foo(val name: String)
 *
 * output:
 * { "root" : { "name" : "string" } }
 *
 * ```
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class SweepWrapper(val value: String = USE_DEFAULT_WRAPPER)

/**
 * An annotation that indicates this class should be unwrapped with the provided name [value] during deserialization.
 *
 * By default, It will use the value from [USE_DEFAULT_UNWRAPPER], which means it will detect the name from
 * [DefaultUnwrapper].
 *
 * This annotation also supports nested unwrapping using dot as delimiter (e.g. "a.b.c").
 *
 * Here are some examples of how this annotation is meant to be used:
 *
 * ```
 * { "foo" : { "name" : "string" } }
 *
 * Can be deserialized into:
 *
 * @SweepUnwrapper(USE_CLASS_NAME_UNWRAPPER)
 * data class Foo(val name: String)
 *
 * ----------
 *
 * { "root" : { "data" : { "name" : "string" } } }
 *
 * Can be deserialized into:
 *
 * @SweepUnwrapper("root.data")
 * data class Foo(val name: String)
 *
 * -----------
 *
 * { "root" : { "name" : "string" } }
 *
 * Can be deserialized into:
 *
 * @SweepUnwrapper // assume that default unwrapper will return "root"
 * data class Foo(val name: String)
 *
 * ```
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class SweepUnwrapper(val value: String = USE_DEFAULT_UNWRAPPER)