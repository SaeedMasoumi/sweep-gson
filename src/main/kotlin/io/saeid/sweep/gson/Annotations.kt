package io.saeid.sweep.gson

import io.saeid.sweep.gson.wrapper.DefaultWrapper

/**
 * An annotation that indicates this class should be wrapped with the provided name [value].
 *
 * By default, It will use the value from [USE_DEFAULT_WRAPPER], which means it will detect the name from
 * [DefaultWrapper].
 *
 * This annotation also support nested wrapping using dot as delimiter (e.g. "a.b.c").
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

@Retention
@Target(AnnotationTarget.CLASS)
annotation class SweepUnwrapper(val value: String = USE_CLASS_NAME_UNWRAPPER)