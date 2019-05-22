package io.saeid.sweep.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

internal interface SweepTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        return create(gson, type, delegate, elementAdapter)
    }

    fun <T> create(
        gson: Gson,
        type: TypeToken<T>,
        delegate: TypeAdapter<T>,
        elementAdapter: TypeAdapter<JsonElement>
    ): TypeAdapter<T>
}