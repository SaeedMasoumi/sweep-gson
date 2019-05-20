package io.saeid.sweep.gson

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.internal.bind.JsonTreeWriter
import com.google.gson.stream.JsonWriter

fun GsonBuilder.withSweep(builder: (Builder.() -> Unit)? = null): GsonBuilder {
    return Builder(this).also { builder?.invoke(it) }.build()
}

internal fun <T> toJsonTree(typeAdapter: TypeAdapter<T>, optionsFrom: JsonWriter, value: T): JsonElement {
    val jsonTreeWriter = JsonTreeWriter()
    jsonTreeWriter.isLenient = optionsFrom.isLenient
    jsonTreeWriter.isHtmlSafe = optionsFrom.isHtmlSafe
    jsonTreeWriter.serializeNulls = optionsFrom.serializeNulls
    typeAdapter.write(jsonTreeWriter, value)
    return jsonTreeWriter.get()
}