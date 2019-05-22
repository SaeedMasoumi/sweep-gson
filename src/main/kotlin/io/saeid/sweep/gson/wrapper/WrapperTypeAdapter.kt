package io.saeid.sweep.gson.wrapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import io.saeid.sweep.gson.SweepTypeAdapter
import io.saeid.sweep.gson.hook.HooksDelegation

internal class WrapperTypeAdapter<T>(
    gson: Gson,
    delegate: TypeAdapter<T>,
    elementAdapter: TypeAdapter<JsonElement>,
    type: TypeToken<T>,
    private val wrapperNamesBuilder: WrapperNamesBuilder,
    private val hooksDelegation: HooksDelegation
) : SweepTypeAdapter<T>(gson, delegate, elementAdapter, type) {

    override fun write(out: JsonWriter, value: T) {
        val wrapperNames = wrapperNamesBuilder.build(value)

        out.beginObject()

        hooksDelegation.preSerialize(out, gson, value)
        wrap(out, wrapperNames) {
            delegate.write(out, value)
        }

        out.endObject()
    }

    private inline fun wrap(out: JsonWriter, wrapperNames: List<String>, block: () -> Unit) {
        val lastItem = wrapperNames.size - 1
        wrapperNames.forEachIndexed { index, s ->
            out.name(s)
            if (index != lastItem)
                out.beginObject()
        }
        block()
        repeat(lastItem) { out.endObject() }
    }
}