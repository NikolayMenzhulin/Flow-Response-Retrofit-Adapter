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
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.typealiases.FlowResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.typealiases.FlowResponseEmpty
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

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
    fun `Check factory creation when return type is FlowResponseEmpty`() {
        val responseType: Type? = getResponseType<FlowResponseEmpty>()
        assertNotNull(responseType)
        assertEquals(Void::class.java, responseType!!)
    }

    @Test
    fun `Check adapter creation when return type is FlowResponse with ResponseBody inside`() {
        val responseType: Type? = getResponseType<FlowResponse<ResponseBody>>()
        assertNotNull(responseType)
        assertEquals(ResponseBody::class.java, responseType!!)
    }

    @Test
    fun `Check adapter creation when return type is FlowResponse with TestResponse inside`() {
        val responseType: Type? = getResponseType<FlowResponse<TestResponse>>()
        assertNotNull(responseType)
        assertEquals(TestResponse::class.java, responseType!!)
    }

    @Test
    fun `Check adapter creation when return type is FlowResponse with List of TestResponse's inside`() {
        val responseType: Type? = getResponseType<FlowResponse<List<TestResponse>>>()
        assertNotNull(responseType)
        assertTrue { responseType is ParameterizedType }

        val listType: ParameterizedType = responseType as ParameterizedType
        assertEquals(List::class.java, listType.rawType)

        val listInnerWildcardType: Type = listType.actualTypeArguments[0]
        assertTrue { listInnerWildcardType is WildcardType }

        val listInnerType: Type = (listInnerWildcardType as WildcardType).upperBounds[0]
        assertEquals(TestResponse::class.java, listInnerType)
    }

    @Test
    fun `Check factory creation when return type is String`() {
        val responseType: Type? = getResponseType<String>()
        assertNull(responseType)
    }

    @Test
    fun `Check factory creation when return type is Flow with String inside`() {
        val responseType: Type? = getResponseType<Flow<String>>()
        assertNull(responseType)
    }

    private inline fun <reified T> getResponseType(): Type? {
        val returnType: Type = object : TypeToken<T>() {}.type
        return factory.get(returnType, NO_ANNOTATIONS, retrofit)?.responseType()
    }
}