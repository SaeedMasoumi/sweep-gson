package io.saeid.sweep.gson.unwrapper

interface DefaultUnwrapper {

    fun force(): Boolean = false

    fun <T> find(type: Class<T>): String?
}