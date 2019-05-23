# Sweep Gson 
[![CircleCI](https://circleci.com/gh/SaeedMasoumi/sweep-gson.svg?style=svg)](https://circleci.com/gh/SaeedMasoumi/sweep-gson) [![codecov](https://codecov.io/gh/SaeedMasoumi/sweep-gson/branch/master/graph/badge.svg)](https://codecov.io/gh/SaeedMasoumi/sweep-gson)
 [ ![Download](https://api.bintray.com/packages/smasoumi/maven/sweep-gson/images/download.svg) ](https://bintray.com/smasoumi/maven/sweep-gson/_latestVersion)

A wrapper/unwrapper extension for Gson

- [Sweep Gson](#sweep-gson)
  * [Download](#download)
  * [Usage](#usage)
    + [SweepWrapper](#sweepwrapper)
      - [Nested Wrapping](#nested-wrapping)
      - [Custom/Default Wrapping](#customdefault-wrapping)
    + [SweepUnwrapper](#sweepunwrapper)
      - [Nested Unwrapping](#nested-unwrapping)
      - [Custom/Default Unwrapping](#customdefault-unwrapping)
      - [startsWith/endsWith](#startswithendswith)
    + [Hooks](#hooks)
  * [Sample](#sample)
  * [Limitations](#limitations)
  
## Download

Gradle:
```groovy
dependencies {
  implementation 'io.saeid.sweep:sweep-gson:0.5.0'
}
```

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

Use `@SweepWrapper` annotation to wrap the object with your desired value during serialization.

```kotlin
@SweepWrapper("request")
data class Request(val name : String)
```

The output after serializing the above class:

```json
{
  "request" : {
    "name": "your_value"
  }
}
```

#### Nested Wrapping

`@SweepWrapper` also supports nested wrapping using dot as delimiter:

For instance, If you replace the value in the above example to` @SweepWrapper("request.data")`, It will generate:

```json
{
  "request": {
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
data class Request(val name : String)
```
```json
{
  "request" : {
    "name": "your_value"
  }
}
```

Also you can define the `@SweepWrapper` value at runtime by overriding `defaultWrapper`. 

```kotlin
GsonBuilder().withSweep {
      defaultWrapper = object : DefaultWrapper {
         override fun <T> wrapWith(value: T): String? {
            return "request.$USE_CLASS_NAME_WRAPPER"
         }
      }
}.create()
```

__Note:__ By default `@SweepWrapper` will switch to the `defaultWrapper`, If you don't pass any value. 

### SweepUnwrapper

Use `@SweepUnwrapper` annotation to unwrap the object with your desired value during deserialization. Unlike `@SweepWrapper`, `@SweepUnwrapper` only works on the root object.

```json
{
  "response" : {
    "name": "your_value"
  }
}
```

For instance, The above JSON can be deserialized to the class below:

```kotlin
@SweepWrapper("response")
data class Response(val name : String)
```

#### Nested Unwrapping

`@SweepUnwrapper` also supports nested unwrapping using dot as delimiter:

For instance, If you replace the value in the above example to` @SweepUnwrapper("response.body")`, It can be extracted by the JSON below:

```json
{
  "response": {
    "body": {
      "name": "your_value"
    }
  }
}
```

#### Custom/Default Unwrapping

Like `@SweepWrapper`, It supports `USE_CLASS_NAME_UNWRAPPER`.

Also you can define the `@SweepUnwrapper` value at runtime by overriding `defaultUnwrapper`. 

```kotlin
GsonBuilder().withSweep {
      defaultUnwrapper = object : DefaultUnwrapper {
        override fun <T> unwrapWith(type: Class<T>): String? = null
      }
      
      override fun force() : Boolean = true

}.create()
```


`@SweepUnwrapper` also supports force-mode, which means It will unwrapp all objects event If they're not annotated with `@SweepUnwrapper` during deserialization. 

If you want to disable force mode for a specific type, you can easily pass `null`.

__Note:__ By default `@SweepUnwrapper` will switch to the `defaultUnwrapper`, If you don't pass any value. 

#### startsWith/endsWith

`@SweepUnwrapper` also supports a simple starts/ends-With regex.

- `@SweepUnwrapper("*Response")` It will unwrap everything ends with `Response`, e.g. `singleResponse`
- `@SweepUnwrapper("response*")` It will unwrap everything starts with `response`, e.g. `responseValue`

### Hooks

__Sweep Gson__ allows to add an object to the root element before serialization by overriding `addToRoot` method from `hooks`:

```kotlin
GsonBuilder().withSweep {
      
      hooks = object : Hooks {
          override fun <T> addToRoot(value: T): Pair<String, Any>? {
             return Pair("properties", Properties(...)
          }
      }
}.create()
```
It will adds `properties` to the root classes annotated with `SweepWrapper`.

```js
{
   "properties" : { 
       ... 
   }
   ...
}
```

## Sample

Assume that you have an REST API with the request/response template below:
```js
// request
{
    "properties": {
        "device": "user's device"
    },
    "request": {
        "request_type": {
	  }
    }
}

// response
{
    "response": {
         "response_typeReply": { 
         }
    }     
}
```

First create our DTOs:

```kotlin
@SweepWrapper
data class Login(val userName: String, val password: String)

@SweepUnwrapper
data class User(val name: String)
```

Then we create our Gson instance using `withSweep`:

```kotlin
    GsonBuilder().withSweep {
        // tell sweep gson to unwrap every object that match `response.*Reply`
        defaultUnwrapper = object : DefaultUnwrapper {
            override fun <T> unwrapWith(type: Class<T>): String? = "response.[the member that ends with Reply]"
            override fun force(): Boolean = true
        }
        // tell sweep gson to wrap every annotated object with `request.[the class name of that object]`        
        defaultWrapper = object : DefaultWrapper {
            override fun <T> wrapWith(value: T): String = "request.$USE_CLASS_NAME_WRAPPER"
        }
        // also add Properties to the root our objects during serialization
        hooks = object : Hooks {
            override fun <T> addToRoot(value: T): Pair<String, Any>? {
                return Pair("properties", Properties("Android"))
            }
        }
    }.create()
```

And now the result:

```kotlin
gson.toJson(Login("admin", "admin")) 
// will prints 
// {"properties":{"device":"Android"},"request":{"login":{"userName":"admin","password":"admin"}}}

gson.fromJson<User>("""{ "response": { "userReply": { "name":"admin" } } }""", User::class.java)
// will prints
// User(name=admin)
```
## Limitations

- __Unwrapper__ only unwraps from the root element.

For example, you __can not__ deserialize the below JSON
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

@SweepUnwrapper("root")
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

- `addToRoot` only works If the root class is annotated with `SweepWrapper`.
- Unlike `SweepUnwrapper`, there is no force mode available for `SweepWrapper`.
