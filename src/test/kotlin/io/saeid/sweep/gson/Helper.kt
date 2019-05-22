package io.saeid.sweep.gson

import io.saeid.sweep.gson.wrapper.DefaultWrapper

internal fun createDefaultWrapper(name: String): DefaultWrapper {
    return object : DefaultWrapper {
        override fun <T> wrapWith(value: T): String? {
            return name
        }
    }
}