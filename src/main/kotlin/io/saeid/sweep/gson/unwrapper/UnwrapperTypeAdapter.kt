package io.saeid.sweep.gson.unwrapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import io.saeid.sweep.gson.SweepTypeAdapter


internal class UnwrapperTypeAdapter<T>(
    gson: Gson,
    delegate: TypeAdapter<T>,
    elementAdapter: TypeAdapter<JsonElement>,
    type: TypeToken<T>,
    private val unwrapperNamesBuilder: UnwrapperNamesBuilder
) : SweepTypeAdapter<T>(gson, delegate, elementAdapter, type) {

    override fun read(`in`: JsonReader): T {
        if (!`in`.isInParentObject()) {
            return delegate.read(`in`)
        }
        val unwrappedElement = unwrap(elementAdapter.read(`in`))
        return delegate.fromJsonTree(unwrappedElement)
    }

    private fun unwrap(original: JsonElement): JsonElement {
        val names = unwrapperNamesBuilder.build(type.rawType)
        var currentElement = original
        names.forEach {
            if (hasMember(currentElement, it)) {
                val jsonObject = currentElement.asJsonObject
                currentElement = jsonObject.get(getMember(jsonObject, it))
            } else {
                return original
            }
        }
        return currentElement
    }

    private fun hasMember(element: JsonElement, member: String): Boolean {
        if (!element.isJsonObject) return false

        val jsonObject = element.asJsonObject

        return when {
            member.startsWith("*") -> {
                val endsWithValue = member.substring(1)
                jsonObject.keySet().any { it.endsWith(endsWithValue) }
            }
            member.endsWith("*") -> {
                val startsWithValue = member.substring(0, member.length - 2)
                jsonObject.keySet().any { it.startsWith(startsWithValue) }
            }
            else -> {
                jsonObject.has(member)
            }
        }
    }

    private fun getMember(jsonObject: JsonObject, member: String): String {
        return when {
            member.startsWith("*") -> {
                val endsWithValue = member.substring(1)
                jsonObject.keySet().first { it.endsWith(endsWithValue) }
            }
            member.endsWith("*") -> {
                val startsWithValue = member.substring(0, member.length - 2)
                jsonObject.keySet().first { it.startsWith(startsWithValue) }
            }
            else -> {
                member
            }
        }
    }

    private fun JsonReader.isInParentObject(): Boolean = path.equals("$", true)
}
