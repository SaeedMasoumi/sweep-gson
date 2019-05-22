package io.saeid.sweep.gson.unwrapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import io.saeid.sweep.gson.SweepTypeAdapter

internal class UnwrapperTypeAdapter<T>(
    gson: Gson,
    delegate: TypeAdapter<T>,
    elementAdapter: TypeAdapter<JsonElement>,
    type: TypeToken<T>,
    private val unwrapperNamesBuilder: UnwrapperNamesBuilder
) : SweepTypeAdapter<T>(gson, delegate, elementAdapter, type)