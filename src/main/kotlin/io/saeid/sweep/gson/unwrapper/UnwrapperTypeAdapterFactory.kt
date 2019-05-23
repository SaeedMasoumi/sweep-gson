package io.saeid.sweep.gson.unwrapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import io.saeid.sweep.gson.SweepReflection.isAnnotationPresent
import io.saeid.sweep.gson.SweepTypeAdapterFactory
import io.saeid.sweep.gson.SweepUnwrapper

internal class UnwrapperTypeAdapterFactory(
    private val defaultUnwrapper: DefaultUnwrapper
) : SweepTypeAdapterFactory {

    private val unwrapperNamesBuilder = UnwrapperNamesBuilder(defaultUnwrapper)

    override fun <T> create(
        gson: Gson,
        type: TypeToken<T>,
        delegate: TypeAdapter<T>,
        elementAdapter: TypeAdapter<JsonElement>
    ): TypeAdapter<T> {
        if (canUnwrap(type.rawType)) {
            return UnwrapperTypeAdapter(gson, delegate, elementAdapter, type, unwrapperNamesBuilder)
        }
        return delegate
    }

    private fun <T> canUnwrap(type: Class<T>): Boolean {
        val hasUnwrapperAnnotation = isAnnotationPresent(type, SweepUnwrapper::class.java)

        if (defaultUnwrapper.force()) {
            val canDetectUnwrapperNames = defaultUnwrapper.unwrapWith(type) != null || hasUnwrapperAnnotation

            if (!canDetectUnwrapperNames) {
                throw IllegalStateException(
                    "default unwrapper is forced but nothing provided." +
                            " Try to implement unwrapWith() method or annotate your class with SweepUnwrapper"
                )
            }
            return true
        }

        return hasUnwrapperAnnotation
    }
}