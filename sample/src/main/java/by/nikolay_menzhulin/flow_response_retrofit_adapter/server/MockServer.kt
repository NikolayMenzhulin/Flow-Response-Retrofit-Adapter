package by.nikolay_menzhulin.flow_response_retrofit_adapter.server

import by.nikolay_menzhulin.flow_response_retrofit_adapter.adapter.ResponseCallAdapterFactory
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS

object MockServer {

    const val SUCCESS_RESPONSE_JSON = """{"result":"Result value."}"""
    const val ERROR_RESPONSE_JSON = """{"error":"Some server error message"}"""

    private val server = MockWebServer()

    lateinit var retrofit: Retrofit
        private set

    suspend fun initRetrofit() {
        withContext(IO) {
            retrofit =
                Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .addCallAdapterFactory(ResponseCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
    }

    fun enqueueResponse(responseCode: Int, responseBody: String? = null) {
        MockResponse()
            .setResponseCode(responseCode)
            .setBodyDelay(delay = 2, unit = SECONDS)
            .apply { responseBody?.let { setBody(it) } }
            .also { server.enqueue(it) }
    }
}