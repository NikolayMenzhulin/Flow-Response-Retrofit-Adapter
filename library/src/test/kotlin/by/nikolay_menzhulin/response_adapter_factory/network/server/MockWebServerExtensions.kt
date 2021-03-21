package by.nikolay_menzhulin.response_adapter_factory.network.server

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import java.util.concurrent.TimeUnit.MILLISECONDS

fun MockWebServer.enqueueResponse(
    responseCode: Int,
    responseBody: String,
    socketPolicy: SocketPolicy? = null
) {
    MockResponse()
        .setResponseCode(responseCode)
        .setBody(responseBody)
        .setBodyDelay(delay = 300, unit = MILLISECONDS)
        .apply { if (responseCode == 204) addHeader("Content-Length", "") }
        .apply { socketPolicy?.let { setSocketPolicy(it) } }
        .also { enqueue(it) }
}