package io.saeid.sweep.gson.unwrapper

import io.saeid.sweep.gson.SweepReflection
import io.saeid.sweep.gson.SweepReflection.isAnnotationPresent
import io.saeid.sweep.gson.SweepUnwrapper
import io.saeid.sweep.gson.USE_CLASS_NAME_UNWRAPPER

class UnwrapperNamesBuilder(private val defaultUnwrapper: DefaultUnwrapper) {

    fun <T> build(value: Class<T>): List<String> {
        val defaultNames = defaultUnwrapper.find(value)
        val sweepUnwrapperEnabled = hasSweepUnwrapperAnnotation(value)

        if (defaultUnwrapper.force()) {
            // when force mode is enable, first priority is @SweepUnwrapper annotation
            if (defaultNames != null && !sweepUnwrapperEnabled) {
                return makeUnwrapperNames(value, defaultNames)
            }
        }
        return makeUnwrapperNames(value, getUnwrapperNameFromAnnotation(value))
    }

    private fun <T> hasSweepUnwrapperAnnotation(value: Class<T>) =
        isAnnotationPresent(value, SweepUnwrapper::class.java)

    private fun <T> getUnwrapperNameFromAnnotation(value: Class<T>): String {
        return SweepReflection.sweepUnwrapperValue(value)
    }

    private fun <T> makeUnwrapperNames(value: Class<T>, rawName: String): List<String> {
        var names = rawName.trim().split(".")
        names = applyClassName(value, names)
        return names
    }

    private fun <T> applyClassName(value: Class<T>, names: List<String>): List<String> {
        return names.map {
            return@map when (it) {
                USE_CLASS_NAME_UNWRAPPER -> {
                    SweepReflection.findClassName(value).decapitalize()
                }
                else -> {
                    it
                }
            }
        }
    }
}