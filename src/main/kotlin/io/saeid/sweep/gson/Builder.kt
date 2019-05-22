package io.saeid.sweep.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import io.saeid.sweep.gson.hook.Hooks
import io.saeid.sweep.gson.wrapper.DefaultWrapper
import io.saeid.sweep.gson.wrapper.WrapperTypeAdapterFactory

interface DefaultUnwrapper {

    fun force(): Boolean = false

    fun find(): String?
}

class Builder internal constructor(private val gsonBuilder: GsonBuilder) {

    private val disabledWrapper = object : DefaultWrapper {
        override fun <T> wrapWith(value: T): String? = null
    }

    private val disabledUnwrapper = object : DefaultUnwrapper {
        override fun find(): String? = null
    }

    private val disabledHooks = object : Hooks {}

    var defaultWrapper: DefaultWrapper = disabledWrapper
    var defaultUnwrapper: DefaultUnwrapper = disabledUnwrapper
    var hooks: Hooks = disabledHooks

    fun build(): GsonBuilder {

        val wrapperTypeAdapterFactory = WrapperTypeAdapterFactory(defaultWrapper, hooks)

        val unwrapperTypeAdapterFactory = object : SweepTypeAdapterFactory {
            override fun <T> create(
                gson: Gson,
                type: TypeToken<T>,
                delegate: TypeAdapter<T>,
                elementAdapter: TypeAdapter<JsonElement>
            ): TypeAdapter<T> {
                return UnwrapperTypeAdapter(
                    gson, delegate, elementAdapter, type, defaultUnwrapper
                )
            }
        }

        gsonBuilder.registerTypeAdapterFactory(wrapperTypeAdapterFactory)
        gsonBuilder.registerTypeAdapterFactory(unwrapperTypeAdapterFactory)

        return gsonBuilder
    }
}