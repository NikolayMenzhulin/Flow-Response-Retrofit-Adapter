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
package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.adapter.FlowResponseCallAdapterFactory
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.network.api.TestApi
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.network.response.TestResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.network.server.enqueueResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.IOException

class ResponseTest {

    private companion object {

        const val EMPTY_BODY_JSON = """{}"""
        const val EMPTY_BODY_LIST_JSON = """[]"""
        const val SUCCESS_RESPONSE_BODY_JSON = """{"mockString":"Test"}"""
        const val SUCCESS_TEST_RESPONSE_JSON = """{"mockString":"Test","mockInt":1234,"mockBoolean":true}"""
        const val SUCCESS_TEST_RESPONSE_LIST_JSON =
            """[
                {"mockString":"Test","mockInt":1234,"mockBoolean":true},
                {"mockString":"Test","mockInt":1234,"mockBoolean":true},
                {"mockString":"Test","mockInt":1234,"mockBoolean":true}
               ]"""
        const val ERROR_RESPONSE_JSON = """{"error":"Some server error message"}"""
    }

    private lateinit var server: MockWebServer
    private lateinit var api: TestApi

    private val expectedTestResponse = TestResponse(mockString = "Test", mockInt = 1234, mockBoolean = true)
    private val expectedListTestResponse = listOf(expectedTestResponse, expectedTestResponse, expectedTestResponse)

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addCallAdapterFactory(FlowResponseCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .apply { api = create() }
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Check response state with 204's code`() {
        checkEmptyResponseState(responseCode = 204)
    }

    @Test
    fun `Check response state with 200's code and empty body`() {
        checkEmptyResponseState(responseCode = 200, emptyResponseBody = EMPTY_BODY_JSON)
    }

    @Test
    fun `Check response state with 200's code and empty body list`() {
        checkEmptyResponseState(responseCode = 200, emptyResponseBody = EMPTY_BODY_LIST_JSON)
    }

    @Test
    fun `Check response state with 200's code and result is ResponseBody`() {
        server.enqueueResponse(responseCode = 200, responseBody = SUCCESS_RESPONSE_BODY_JSON)

        val responseStates: MutableList<Response<ResponseBody>> = mutableListOf()
        runBlocking { api.responseWithResponseBody().collect(responseStates::add) }

        checkSuccessResponseState(responseStates)
        assertEquals(SUCCESS_RESPONSE_BODY_JSON, responseStates[1].getData().string())
        assertNotNull(responseStates[1].getDataOrNull())
    }

    @Test
    fun `Check response state with 200's code and result is TestResponse`() {
        val responseStates = getResponseStates(responseCode = 200, responseBody = SUCCESS_TEST_RESPONSE_JSON)
        checkSuccessResponseState(responseStates)
        assertEquals(expectedTestResponse, responseStates[1].getData())
        assertNotNull(responseStates[1].getDataOrNull())
    }

    @Test
    fun `Check response state with 200's code and result is List of TestResponse`() {
        server.enqueueResponse(responseCode = 200, responseBody = SUCCESS_TEST_RESPONSE_LIST_JSON)

        val responseStates: MutableList<Response<List<TestResponse>>> = mutableListOf()
        runBlocking { api.responseWithDataList().collect(responseStates::add) }

        checkSuccessResponseState(responseStates)
        assertEquals(expectedListTestResponse, responseStates[1].getData())
        assertNotNull(responseStates[1].getDataOrNull())
    }

    @Test
    fun `Check response state with 500's`() {
        checkErrorResponseState<HttpException>(responseCode = 500)
    }

    @Test
    fun `Check response state when connection problem`() {
        checkErrorResponseState<IOException>(responseCode = 200, hasConnectionProblem = true)
    }

    private fun getResponseStates(
        responseCode: Int,
        responseBody: String? = null,
        socketPolicy: SocketPolicy? = null
    ): List<Response<TestResponse>> {
        server.enqueueResponse(responseCode, responseBody, socketPolicy)
        return runBlocking {
            val responseStates: MutableList<Response<TestResponse>> = mutableListOf()
            api.run {
                if (responseBody == null || responseBody == EMPTY_BODY_JSON || responseBody == EMPTY_BODY_LIST_JSON) {
                    responseWithoutData()
                } else {
                    responseWithData()
                }
            }.collect(responseStates::add)
            responseStates
        }
    }

    private fun checkEmptyResponseState(responseCode: Int, emptyResponseBody: String? = null) {
        val responseStates = getResponseStates(responseCode, emptyResponseBody)
        assertTrue { responseStates[0].isLoading }
        assertTrue { responseStates[1].isEmpty }
        assertNoData(responseStates[1])
        assertNoError(responseStates[1])
    }

    private fun <T> checkSuccessResponseState(responseStates: List<Response<T>>) {
        assertTrue { responseStates[0].isLoading }
        assertTrue { responseStates[1].isSuccess }
        assertNoError(responseStates[1])
    }

    private inline fun <reified T : Throwable> checkErrorResponseState(
        responseCode: Int,
        hasConnectionProblem: Boolean = false
    ) {
        val socketPolicy: SocketPolicy? = if (hasConnectionProblem) DISCONNECT_AFTER_REQUEST else null
        val responseStates = getResponseStates(
            responseCode = responseCode,
            responseBody = ERROR_RESPONSE_JSON,
            socketPolicy = socketPolicy
        )
        assertTrue { responseStates[0].isLoading }
        assertTrue { responseStates[1].isError }
        assertNoData(responseStates[1])
        assertTrue { responseStates[1].getError() is T }
        assertNotNull(responseStates[1].getErrorOrNull())
    }

    private fun <T> assertNoError(response: Response<T>) {
        assertThrows<IllegalStateException> { response.getError() }
        assertNull(response.getErrorOrNull())
    }

    private fun <T> assertNoData(response: Response<T>) {
        assertThrows<IllegalStateException> { response.getData() }
        assertNull(response.getDataOrNull())
    }
}