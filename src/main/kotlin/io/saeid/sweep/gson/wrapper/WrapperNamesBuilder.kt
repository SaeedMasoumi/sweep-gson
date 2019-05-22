package io.saeid.sweep.gson.wrapper

import io.saeid.sweep.gson.SweepReflection
import io.saeid.sweep.gson.SweepReflection.isAnnotationPresent
import io.saeid.sweep.gson.SweepWrapper
import io.saeid.sweep.gson.USE_CLASS_NAME_WRAPPER
import io.saeid.sweep.gson.USE_DEFAULT_WRAPPER

/**
 * Builds a list of wrapper names based on [SweepWrapper.value].
 */
internal class WrapperNamesBuilder(private val defaultWrapper: DefaultWrapper) {

    fun <T> build(value: T): List<String> {
        if (!isAnnotationPresent(value, SweepWrapper::class.java)) {
            throw IllegalStateException("Class $value must be annotation with SweepWrapper.")
        }

        var names = findWrapperNamesFromAnnotation(value)
        names = applyDefaultWrapper(value, names)
        names = applyClassNameWrapper(value, names)

        return names
    }

    private fun <T> findWrapperNamesFromAnnotation(value: T): List<String> {
        return SweepReflection.sweepWrapperValue(value).trim().split(".")
    }

    private fun <T> applyDefaultWrapper(value: T, oldNames: List<String>): List<String> {
        val newNames = mutableListOf<String>()

        oldNames.forEach {
            when (it) {
                USE_DEFAULT_WRAPPER -> {
                    val defaultWrappers = defaultWrapper.wrapWith(value)?.trim()?.split(".")
                        ?: throw IllegalStateException(
                            "$value forced to use default wrapper, but nothing provided." +
                                    " Try to implement DefaultWrapper"
                        )
                    newNames.addAll(defaultWrappers)
                }
                else -> {
                    newNames.add(it)
                }
            }
        }
        return newNames
    }

    private fun <T> applyClassNameWrapper(value: T, names: List<String>): List<String> {
        return names.map {
            return@map when (it) {
                USE_CLASS_NAME_WRAPPER -> {
                    SweepReflection.findClassName(value).decapitalize()
                }
                else -> {
                    it
                }
            }
        }
    }
}