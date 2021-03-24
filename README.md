# Flow-Response-Retrofit-Adapter

A small library containing a wrapper over network requests implemented using Kotlin Flow  
and an adapter that allows this wrapper to be used along with the Retrofit 2 library.

[![build](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/actions/workflows/ci-build.yml/badge.svg)](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/actions/workflows/ci-build.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.nikolaymenzhulin/flow-response-retrofit-adapter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.nikolaymenzhulin/flow-response-retrofit-adapter)
[![License](https://img.shields.io/badge/license-Apache%202.0-dark.svg)](http://www.apache.org/licenses/LICENSE-2.0)
## Usage

Add [FlowResponseCallAdapterFactory](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/blob/main/library/src/main/java/com/github/nikolaymenzhulin/flow_response_retrofit_adapter/adapter/FlowResponseCallAdapterFactory.kt) to your Retrofit configuration:
```kotlin
Retrofit.Builder()
    // Some other configuration.
    .addCallAdapterFactory(FlowResponseCallAdapterFactory())
    .build()
```

In your Retrofit interfaces wrap results of your requests in [FlowResponse](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/blob/main/library/src/main/java/com/github/nikolaymenzhulin/flow_response_retrofit_adapter/response/FlowResponse.kt):
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
                // Logic of changing loading state or something else.
            }
            state.isSuccess -> {
                val data: SomeNetworkRequestResult = state.getData()
                if (data.isNotEmpty()) {
                    // Logic of reaction to success response and using data from it...
                } else {
                    // or reaction on empty data.
                }
            }
            state.isError -> {
                val error: Throwable = state.getError()
                // Logic of errors handling.
            }
        }
    }.launchIn(coroutineScope)
```

More information of usage in [sample](https://github.com/NikolayMenzhulin/Flow-Response-Retrofit-Adapter/tree/main/sample).

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
    implementation 'com.github.nikolaymenzhulin:flow-response-retrofit-adapter:1.0.0'
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
