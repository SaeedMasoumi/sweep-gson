package io.saeid.sweep.gson.wrapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import io.saeid.sweep.gson.SweepReflection.isAnnotationPresent
import io.saeid.sweep.gson.SweepTypeAdapterFactory
import io.saeid.sweep.gson.SweepWrapper
import io.saeid.sweep.gson.hook.Hooks
import io.saeid.sweep.gson.hook.HooksDelegation

internal class WrapperTypeAdapterFactory(
    defaultWrapper: DefaultWrapper,
    hooks: Hooks
) : SweepTypeAdapterFactory {

    private val wrapperNameBuilder = WrapperNamesBuilder(defaultWrapper)
    private val hooksDelegation = HooksDelegation(hooks)

    override fun <T> create(
        gson: Gson,
        type: TypeToken<T>,
        delegate: TypeAdapter<T>,
        elementAdapter: TypeAdapter<JsonElement>
    ): TypeAdapter<T> {
        if (isAnnotationPresent(type.rawType, SweepWrapper::class.java)) {
            return WrapperTypeAdapter(gson, delegate, elementAdapter, type, wrapperNameBuilder, hooksDelegation)
        }
        return delegate
    }
}