package io.saeid.sweep.gson.unwrapper

import io.saeid.sweep.gson.SweepReflection
import io.saeid.sweep.gson.SweepUnwrapper
import io.saeid.sweep.gson.USE_CLASS_NAME_UNWRAPPER
import io.saeid.sweep.gson.USE_DEFAULT_UNWRAPPER

internal class UnwrapperNamesBuilder(private val defaultUnwrapper: DefaultUnwrapper) {

    fun <T> build(value: Class<T>): List<String> {
        val names = findFromAnnotation(value)
        if (names.isEmpty()) {
            return findFromDefaultUnwrapper(value)
        }
        return names
    }

    private fun <T> findFromAnnotation(value: Class<T>): List<String> {
        if (SweepReflection.isAnnotationPresent(value, SweepUnwrapper::class.java)) {
            var annotationNames = SweepReflection.sweepUnwrapperValue(value).trim().split(".")
            annotationNames = applyDefaultWrapperWhileAnnotationPresent(value, annotationNames)
            annotationNames = applyClassNameWrapper(value, annotationNames)
            return annotationNames
        }
        return emptyList()
    }

    private fun <T> findFromDefaultUnwrapper(value: Class<T>): List<String> {
        val names = getDefaultUnwrapperNames(value)
        if (!names.isNullOrEmpty()) {
            return applyClassNameWrapper(value, names)
        }
        return emptyList()
    }

    private fun <T> applyDefaultWrapperWhileAnnotationPresent(value: Class<T>, oldNames: List<String>): List<String> {
        val newNames = mutableListOf<String>()

        oldNames.forEach {
            when (it) {
                USE_DEFAULT_UNWRAPPER -> {
                    val defaultUnwrappers = getDefaultUnwrapperNames(value)
                        ?: throw IllegalStateException(
                            "$value forced to use default unwrapper, but nothing provided." +
                                    " Try to implement DefaultUnWrapper"
                        )
                    newNames.addAll(defaultUnwrappers)
                }
                else -> {
                    newNames.add(it)
                }
            }
        }
        return newNames
    }

    private fun <T> applyClassNameWrapper(value: Class<T>, names: List<String>): List<String> {
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

    private fun <T> getDefaultUnwrapperNames(value: Class<T>): List<String>? {
        return defaultUnwrapper.unwrapWith(value)?.trim()?.split(".")
    }
}