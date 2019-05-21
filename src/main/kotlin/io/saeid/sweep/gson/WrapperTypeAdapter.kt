package io.saeid.sweep.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import io.saeid.sweep.gson.SweepReflection.*

internal class WrapperTypeAdapter<T>(
    gson: Gson,
    delegate: TypeAdapter<T>,
    elementAdapter: TypeAdapter<JsonElement>,
    type: TypeToken<T>,
    private val defaultWrapper: DefaultWrapper
) : SweepTypeAdapter<T>(gson, delegate, elementAdapter, type) {

    override fun write(out: JsonWriter, value: T) {
        if (isAnnotationPresent(value, SweepWrapper::class.java)) {
            val wrapperNames = getWrapperNames(value)

            val original = toJsonTree(delegate, out, value)
            val root = JsonObject()

            wrap(root, original, wrapperNames)

            gson.toJson(root, out)
        } else {
            super.write(out, value)
        }
    }

    private fun getWrapperNames(value: T): List<String> {
        var names = findWrapperNamesFromAnnotation(value)
        names = applyDefaultWrapper(value, names)
        names = applyClassNameWrapper(value, names)

        return names
    }

    private fun findWrapperNamesFromAnnotation(value: T): List<String> {
        return sweepWrapperValue(value).trim().split(".")
    }

    private fun applyDefaultWrapper(value: T, oldNames: List<String>): List<String> {
        val newNames = mutableListOf<String>()

        oldNames.forEach {
            when (it) {
                USE_DEFAULT_WRAPPER -> {
                    val defaultWrappers = defaultWrapper.find(value)?.trim()?.split(".")
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

    private fun applyClassNameWrapper(value: T, names: List<String>): List<String> {
        return names.map {
            return@map when (it) {
                USE_CLASS_NAME_WRAPPER -> {
                    findClassName(value).decapitalize()
                }
                else -> {
                    it
                }
            }
        }
    }

    private fun wrap(root: JsonObject, element: JsonElement, wrapperNames: List<String>) {
        val lastIndex = wrapperNames.size - 1

        var currentObject = root

        wrapperNames.forEachIndexed { index, name ->
            if (index == lastIndex) {
                currentObject.add(name, element)
            } else {
                val newObject = JsonObject()
                currentObject.add(name, newObject)
                // move element inside a new object
                currentObject = newObject
            }
        }
    }
}
