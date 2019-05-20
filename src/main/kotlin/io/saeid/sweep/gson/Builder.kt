package io.saeid.sweep.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken

interface DefaultWrapper {
    fun <T> find(value: T): String?
}

interface DefaultUnwrapper {

    fun force(): Boolean = false

    fun find(): String?
}

class Builder internal constructor(private val gsonBuilder: GsonBuilder) {

    private val disabledWrapper = object : DefaultWrapper {
        override fun <T> find(value: T): String? = null
    }

    private val disabledUnwrapper = object : DefaultUnwrapper {
        override fun find(): String? = null
    }

    var defaultWrapper: DefaultWrapper = disabledWrapper
    var defaultUnwrapper: DefaultUnwrapper = disabledUnwrapper

    fun build(): GsonBuilder {

        val wrapperTypeAdapterFactory = object : SweepTypeAdapterFactory {
            override fun <T> create(gson: Gson,
                                    type: TypeToken<T>,
                                    delegate: TypeAdapter<T>,
                                    elementAdapter: TypeAdapter<JsonElement>,
                                    rawType: TypeToken<T>
            ) =
                    WrapperTypeAdapter(
                            gson, delegate, elementAdapter, rawType, defaultWrapper
                    )
        }

        val unwrapperTypeAdapterFactory = object : SweepTypeAdapterFactory {
            override fun <T> create(gson: Gson,
                                    type: TypeToken<T>,
                                    delegate: TypeAdapter<T>,
                                    elementAdapter: TypeAdapter<JsonElement>,
                                    rawType: TypeToken<T>
            ) =
                    UnwrapperTypeAdapter(
                            gson, delegate, elementAdapter, rawType, defaultUnwrapper
                    )
        }

        gsonBuilder.registerTypeAdapterFactory(wrapperTypeAdapterFactory)
        gsonBuilder.registerTypeAdapterFactory(unwrapperTypeAdapterFactory)

        return gsonBuilder
    }
}