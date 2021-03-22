package by.nikolay_menzhulin.flow_response_retrofit_adapter.network.server

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import java.util.concurrent.TimeUnit.MILLISECONDS

fun MockWebServer.enqueueResponse(
    responseCode: Int,
    responseBody: String? = null,
    socketPolicy: SocketPolicy? = null
) {
    MockResponse()
        .setResponseCode(responseCode)
        .setBodyDelay(delay = 300, unit = MILLISECONDS)
        .apply { responseBody?.let { setBody(it) } }
        .apply { socketPolicy?.let { setSocketPolicy(it) } }
        .also { enqueue(it) }
}