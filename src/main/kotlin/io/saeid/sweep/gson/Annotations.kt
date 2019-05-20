package io.saeid.sweep.gson

@Retention
@Target(AnnotationTarget.CLASS)
annotation class SweepWrapper(val value: String = USE_DEFAULT_WRAPPER)

@Retention
@Target(AnnotationTarget.CLASS)
annotation class SweepUnwrapper(val value: String = USE_DEFAULT_UNWRAPPER)