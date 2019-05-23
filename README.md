# Sweep Gson 
[![CircleCI](https://circleci.com/gh/SaeedMasoumi/sweep-gson.svg?style=svg)](https://circleci.com/gh/SaeedMasoumi/sweep-gson) [![codecov](https://codecov.io/gh/SaeedMasoumi/sweep-gson/branch/master/graph/badge.svg)](https://codecov.io/gh/SaeedMasoumi/sweep-gson)

A wrapper/unwrapper extension for Gson

## Download

## Usage

```kotlin

GsonBuilder().withSweep().create()
```

If you want more advance features:

```kotlin
GsonBuilder().withSweep {
      defaultWrapper = ...    // optional
      defaultUnwrapper = ...  // optional
      hooks = ...             // optional
}.create()
```

### SweepWrapper

Use `@SweepWrapper` annotation to wrap the object with your desire value during serialization.

```kotlin
@SweepWrapper("root")
data class Child(val name : String)
```

The output after serializing the above class:

```json
{
  "root" : {
    "name": "your_value"
  }
}
```

#### Nested Wrapping

`@SweepWrapper` also supports nested wrapping using dot as delimiter:

For instance, If you replace the value in the above example to` @SweepWrapper("root.data")`, It will generates:

```json
{
  "root": {
    "data": {
      "name": "your_value"
    }
  }
}
```
#### Custom/Default Wrapping

If you want to use the class name as the wrapper value you can simply use `@SweepWrapper(USE_CLASS_NAME_WRAPPER)`.

`USE_CLASS_NAME_WRAPPER` is a reserved word which will force `@SweepWrapper` to use the class name (decapitalized version) as the wrapper name.

For instance:
```kotlin
@SweepWrapper(USE_CLASS_NAME_WRAPPER)
data class Child(val name : String)
```
```json
{
  "child" : {
    "name": "your_value"
  }
}
```

Also you can define the `@SweepWrapper` value at runtime by overriding `defaultWrapper`. 

```kotlin
GsonBuilder().withSweep {
      defaultWrapper = object : DefaultWrapper {
         override fun <T> wrapWith(value: T): String? {
            return "root.$USE_CLASS_NAME_WRAPPER"
         }
      }
}.create()
```

__Note:__ By default `@SweepWrapper` will switch to the `defaultWrapper`, If you don't pass any value. 

### SweepUnwrapper

### Hooks

## Limitations

- __Unwrapper__ only unwraps from the root element.

For example, you __can not__ deserialize the below Json
```json
{
  "parent": {
    "root" : {
      "name" : "sweep"
    }
  }
}
```
to 

```kotlin
data class Root(val parent : Parent)

data class Parent(val child : Child)

@SweepUnwrapepr("root")
data class Child(val name : String)
```

- __Unwrapper__ will ignore sibling elements while deserializing.

For example, `version` will be null after deserialization, but `child` will be deserialized.
```json
{
  "parent": {
    "root" : {
      "name" : "sweep"
    }
  }
}
```
```kotlin
@SweepUnwrapper("root")
data class Root(val version : String, val child : Child)

data class Child(val name : String)
```
