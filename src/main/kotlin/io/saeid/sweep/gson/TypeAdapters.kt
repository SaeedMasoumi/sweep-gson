package io.saeid.sweep.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

internal interface SweepTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        return create(gson, type, delegate, elementAdapter, type)
    }

    fun <T> create(
        gson: Gson,
        type: TypeToken<T>,
        delegate: TypeAdapter<T>,
        elementAdapter: TypeAdapter<JsonElement>,
        rawType: TypeToken<T>
    ): SweepTypeAdapter<T>
}

/**
 * Default implementation of [TypeAdapter] to pass reading and writing to the delegated adapter.
 */
internal abstract class SweepTypeAdapter<T>(
    protected val gson: Gson,
    protected val delegate: TypeAdapter<T>,
    protected val elementAdapter: TypeAdapter<JsonElement>,
    protected val type: TypeToken<T>
) : TypeAdapter<T>() {

    override fun write(out: JsonWriter, value: T) {
        delegate.write(out, value)
    }

    override fun read(`in`: JsonReader): T {
        return delegate.fromJsonTree(elementAdapter.read(`in`))
    }
}