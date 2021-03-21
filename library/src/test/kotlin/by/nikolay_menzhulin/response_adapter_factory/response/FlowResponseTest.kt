package by.nikolay_menzhulin.response_adapter_factory.response

import by.nikolay_menzhulin.response_adapter_factory.adapter.ResponseCallAdapterFactory
import by.nikolay_menzhulin.response_adapter_factory.network.TestService
import by.nikolay_menzhulin.response_adapter_factory.network.response.TestResponse
import by.nikolay_menzhulin.response_adapter_factory.network.server.enqueueResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
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

internal class FlowResponseTest {

    private companion object {

        const val RESPONSE_JSON = """{"mockString":"Test","mockInt":1234,"mockBoolean":true}"""
        const val EMPTY_RESPONSE_JSON = """{}"""
        const val ERROR_RESPONSE_JSON = """{"error":"Some server error message"}"""
    }

    private lateinit var server: MockWebServer
    private lateinit var testService: TestService

    private val expectedResponse = TestResponse(mockString = "Test", mockInt = 1234, mockBoolean = true)

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addCallAdapterFactory(ResponseCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .apply { testService = create() }
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Check response states count is always 2`() {

        fun checkSuccessResponseWithResult() {
            val responseStates = getResponseStates(responseCode = 200, responseBody = RESPONSE_JSON)
            assertEquals(2, responseStates.size)
        }

        fun checkSuccessResponseWithoutResult() {
            val responseStates = getResponseStates(responseCode = 200, responseBody = EMPTY_RESPONSE_JSON)
            assertEquals(2, responseStates.size)
        }

        fun checkErrorResponse() {
            val responseStates = getResponseStates(responseCode = 500, responseBody = RESPONSE_JSON)
            assertEquals(2, responseStates.size)
        }

        checkSuccessResponseWithResult()
        checkSuccessResponseWithoutResult()
        checkErrorResponse()
    }

    @Test
    fun `Check first response state is Loading`() {
        val responseStates = getResponseStates(responseCode = 200, responseBody = RESPONSE_JSON)
        assertTrue { responseStates[0].isLoading }
    }

    @Test
    fun `Check second response state is Success or Error`() {

        fun checkSuccessResponseWithResult() {
            val responseStates = getResponseStates(responseCode = 200, responseBody = RESPONSE_JSON)
            assertTrue { responseStates[1].isSuccess }
        }

        fun checkSuccessResponseWithoutResult() {
            val responseStates = getResponseStates(responseCode = 200, responseBody = EMPTY_RESPONSE_JSON)
            assertTrue { responseStates[1].isSuccess }
        }

        fun checkErrorResponse() {
            val responseStates = getResponseStates(responseCode = 500, responseBody = RESPONSE_JSON)
            assertTrue { responseStates[1].isError }
        }

        checkSuccessResponseWithResult()
        checkSuccessResponseWithoutResult()
        checkErrorResponse()
    }

    @Test
    fun `Check success response result was got`() {
        val responseStates = getResponseStates(responseCode = 200, responseBody = RESPONSE_JSON)
        val actualResponse: TestResponse = responseStates[1].getData()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `Check success empty response result wasn't got`() {
        val responseStates = getResponseStates(responseCode = 200, responseBody = EMPTY_RESPONSE_JSON)
        assertThrows<IllegalStateException> { responseStates[1].getData() }
        assertNull(responseStates[1].getDataOrNull())
    }

    @Test
    fun `Check failed response error was got`() {
        val responseStates = getResponseStates(responseCode = 500, responseBody = ERROR_RESPONSE_JSON)
        val error: Throwable = responseStates[1].getError()
        assertEquals(HttpException::class.java, error::class.java)
    }

    @Test
    fun `Check success response error wasn't got`() {
        val responseStates = getResponseStates(responseCode = 200, responseBody = RESPONSE_JSON)
        assertThrows<IllegalStateException> { responseStates[1].getError() }
        assertNull(responseStates[1].getErrorOrNull())
    }

    @Test
    fun `Check error was got when connection problem`() {
        val responseStates = getResponseStates(
            responseCode = 200,
            responseBody = RESPONSE_JSON,
            socketPolicy = DISCONNECT_AFTER_REQUEST
        )
        val error: Throwable = responseStates[1].getError()
        assertEquals(IOException::class.java, error::class.java)
    }

    private fun getResponseStates(
        responseCode: Int,
        responseBody: String,
        socketPolicy: SocketPolicy? = null
    ): List<Response<TestResponse>> {
        server.enqueueResponse(responseCode, responseBody, socketPolicy)
        return runBlocking {
            val responseStates: MutableList<Response<TestResponse>> = mutableListOf()
            testService.run {
                if (responseBody == EMPTY_RESPONSE_JSON) {
                    responseWithoutData()
                } else {
                    responseWithData()
                }
            }.collect(responseStates::add)
            responseStates
        }
    }
}