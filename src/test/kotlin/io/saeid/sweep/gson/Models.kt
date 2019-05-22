package io.saeid.sweep.gson

data class NoAnnotation(val name: String)

@SweepWrapper
data class Child(val name: String)

@SweepWrapper
data class Root(val name: String, val child: Child)

data class Properties(val id: String)