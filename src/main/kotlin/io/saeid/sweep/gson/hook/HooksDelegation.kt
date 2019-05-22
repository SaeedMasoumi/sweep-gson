package io.saeid.sweep.gson.hook

import com.google.gson.Gson
import com.google.gson.stream.JsonWriter

internal class HooksDelegation(private val hooks: Hooks) {

    fun <T> preSerialize(out: JsonWriter, gson: Gson, original: T) {
        hooks.addToRoot(original)?.let {
            val (rootName, value) = it
            val rootValue = gson.toJsonTree(value)

            out.name(rootName)
            gson.toJson(rootValue, out)
        }
    }
}