package io.saeid.sweep.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import io.saeid.sweep.gson.SweepReflection.sweepUnwrapperValue

internal class UnwrapperTypeAdapter<T>(
    gson: Gson,
    delegate: TypeAdapter<T>,
    elementAdapter: TypeAdapter<JsonElement>,
    type: TypeToken<T>,
    private val defaultUnwrapper: DefaultUnwrapper
) :
    SweepTypeAdapter<T>(gson, delegate, elementAdapter, type) {

    override fun read(`in`: JsonReader): T {
        val jsonElement = unwrap(elementAdapter.read(`in`))
        return delegate.fromJsonTree(jsonElement)
    }

    private fun unwrap(original: JsonElement): JsonElement {
        return try {
            val names = getUnwrapperNames()
            return unwrapFromNames(original, names)
        } catch (e: Exception) {
            original
        }
    }

    private fun getUnwrapperNames(): List<String> {
        val defaultNames = defaultUnwrapper.find()
        val sweepUnwrapperEnabled = hasSweepUnwrapperAnnotation()

        if (defaultUnwrapper.force() && !sweepUnwrapperEnabled) {
            if (defaultNames.isNullOrEmpty())
                throw IllegalStateException(
                    "default unwrapper is forced but nothing provided." +
                            " Try to implement wrapWith() method"
                )
            return makeUnwrapperNames(defaultNames)
        }

        if (sweepUnwrapperEnabled) {
            return makeUnwrapperNames(getUnwrapperNameFromAnnotation())
        }
        return emptyList()
    }

    private fun unwrapFromNames(original: JsonElement, names: List<String>): JsonElement {
        if (names.isEmpty()) return original

        var new = original

        names.forEach { member ->
            findMember(new, member)?.let { new = it } ?: return original
        }

        return new
    }

    private fun findMember(element: JsonElement, member: String): JsonElement? {
        if (!element.isJsonObject) {
            return null
        }
        val jsonObject = element.asJsonObject
        if (jsonObject.has(member)) {
            return jsonObject.get(member)
        }
        return null
    }

    private fun hasSweepUnwrapperAnnotation() = SweepReflection.isAnnotationPresent(
        type.rawType,
        SweepUnwrapper::class.java
    )

    private fun getUnwrapperNameFromAnnotation(): String {
        return sweepUnwrapperValue(type.rawType)
    }

    private fun makeUnwrapperNames(rawName: String): List<String> {
        return rawName.trim().split(".")
    }
}