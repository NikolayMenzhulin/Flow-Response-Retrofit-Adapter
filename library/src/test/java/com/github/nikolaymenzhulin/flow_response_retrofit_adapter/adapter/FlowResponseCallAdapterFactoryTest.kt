/*
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
*/
package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.adapter

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.network.response.TestResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowEmptyResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowResponse
import com.google.gson.reflect.TypeToken
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class FlowResponseCallAdapterFactoryTest {

    private companion object {

        val NO_ANNOTATIONS: Array<Annotation> = emptyArray()
    }

    private lateinit var retrofit: Retrofit

    private val server = MockWebServer()
    private val factory = FlowResponseCallAdapterFactory()

    @BeforeEach
    fun setUp() {
        retrofit =
            Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(FlowResponseCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Test
    fun `Check factory creation with FlowResponse return type`() {
        val returnType: Type = object : TypeToken<FlowResponse<TestResponse>>() {}.type
        val responseType: Type? = factory.get(returnType, NO_ANNOTATIONS, retrofit)?.responseType()
        assertNotNull(responseType)
        assertEquals(TestResponse::class.java, responseType!!)
    }

    @Test
    fun `Check factory creation with FlowEmptyResponse return type`() {
        val returnType: Type = object : TypeToken<FlowEmptyResponse>() {}.type
        val responseType: Type? = factory.get(returnType, NO_ANNOTATIONS, retrofit)?.responseType()
        assertNotNull(responseType)
        assertEquals(Void::class.java, responseType!!)
    }

    @Test
    fun `Check factory creation with non FlowResponse return type`() {
        val returnType: Type = object : TypeToken<String>() {}.type
        val responseType: Type? = factory.get(returnType, NO_ANNOTATIONS, retrofit)?.responseType()
        assertNull(responseType)
    }
}