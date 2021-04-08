# Flow-Response-Retrofit-Adapter

The small library containing the wrapper over network requests implemented using Kotlin Flow  
and the adapter that allows this wrapper to be used along with Retrofit 2.

[![build](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/actions/workflows/ci-build.yml/badge.svg)](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/actions/workflows/ci-build.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.nikolaymenzhulin/flow-response-retrofit-adapter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.nikolaymenzhulin/flow-response-retrofit-adapter)
[![License](https://img.shields.io/badge/license-Apache%202.0-dark.svg)](http://www.apache.org/licenses/LICENSE-2.0)
## Usage

Add [FlowResponseCallAdapterFactory](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/blob/main/library/src/main/java/com/github/nikolaymenzhulin/flow_response_retrofit_adapter/adapter/FlowResponseCallAdapterFactory.kt) to your Retrofit configuration:
```kotlin
Retrofit.Builder()
    // Some another configuration.
    .addCallAdapterFactory(FlowResponseCallAdapterFactory())
    .build()
```

In your Retrofit interfaces wrap results of your requests in [FlowResponse](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/blob/main/library/src/main/java/com/github/nikolaymenzhulin/flow_response_retrofit_adapter/typealiases/ResponseTypealiases.kt):
```kotlin
interface SomeRetrofitService {
    
    @GET("/")
    fun someNetworkRequest(): FlowResponse<SomeNetworkRequestResult>
    
    // Some other methods.
}
```

Use Kotlin Coroutines for work with Flows and receive [Response](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/blob/main/library/src/main/java/com/github/nikolaymenzhulin/flow_response_retrofit_adapter/response/Response.kt) states of your requests:
```kotlin
someRetrofitService.someNetworkRequest()
    .onEach { state ->
        when {
            state.isLoading -> {
                // Logic of changing the loading state or something else.
            }
            state.isSuccess -> {
                if (!state.isEmpty) {
                    val data: SomeNetworkRequestResult = state.getData()
                    // Logic of reaction to the success response and using the data from it...
                } else {
                    // or reaction on the empty data.
                }
            }
            state.isError -> {
                val error: Throwable = state.getError()
                // Logic of errors handling.
            }
        }
    }.launchIn(coroutineScope)
```

More information of usage into the [sample](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/tree/main/sample).

## Download

**Step 1.** Add the Maven Central repository to your build file:
```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

**Step 2.** Add the dependency:
```groovy
dependencies {
    implementation 'com.github.nikolaymenzhulin:flow-response-retrofit-adapter:1.2.1'
}
```

## License

```
Copyright © 2021 Nikolay Menzhulin.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
