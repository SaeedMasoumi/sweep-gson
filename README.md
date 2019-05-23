# Sweep Gson 
[![CircleCI](https://circleci.com/gh/SaeedMasoumi/sweep-gson.svg?style=svg)](https://circleci.com/gh/SaeedMasoumi/sweep-gson) [![codecov](https://codecov.io/gh/SaeedMasoumi/sweep-gson/branch/master/graph/badge.svg)](https://codecov.io/gh/SaeedMasoumi/sweep-gson)

A wrapper/unwrapper extension for Gson

## Download

## Usage

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