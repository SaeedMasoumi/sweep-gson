package io.saeid.sweep.gson

import com.google.gson.GsonBuilder
import io.saeid.sweep.gson.hook.Hooks
import io.saeid.sweep.gson.unwrapper.DefaultUnwrapper
import io.saeid.sweep.gson.unwrapper.UnwrapperTypeAdapterFactory
import io.saeid.sweep.gson.wrapper.DefaultWrapper
import io.saeid.sweep.gson.wrapper.WrapperTypeAdapterFactory

class Builder internal constructor(private val gsonBuilder: GsonBuilder) {

    private val disabledWrapper = object : DefaultWrapper {
        override fun <T> wrapWith(value: T): String? = null
    }

    private val disabledUnwrapper = object : DefaultUnwrapper {
        override fun <T> find(type: Class<T>): String? = null
    }

    private val disabledHooks = object : Hooks {}

    var defaultWrapper: DefaultWrapper = disabledWrapper
    var defaultUnwrapper: DefaultUnwrapper = disabledUnwrapper
    var hooks: Hooks = disabledHooks

    fun build(): GsonBuilder {

        val wrapperTypeAdapterFactory = WrapperTypeAdapterFactory(defaultWrapper, hooks)

        val unwrapperTypeAdapterFactory = UnwrapperTypeAdapterFactory(defaultUnwrapper)

        gsonBuilder.registerTypeAdapterFactory(wrapperTypeAdapterFactory)
        gsonBuilder.registerTypeAdapterFactory(unwrapperTypeAdapterFactory)

        return gsonBuilder
    }
}