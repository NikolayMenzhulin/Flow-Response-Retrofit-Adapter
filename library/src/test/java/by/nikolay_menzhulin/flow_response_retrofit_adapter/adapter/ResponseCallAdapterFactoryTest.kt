package by.nikolay_menzhulin.flow_response_retrofit_adapter.adapter

import by.nikolay_menzhulin.flow_response_retrofit_adapter.network.response.TestResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowEmptyResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowResponse
import com.google.gson.reflect.TypeToken
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

internal class ResponseCallAdapterFactoryTest {

    private companion object {

        val NO_ANNOTATIONS: Array<Annotation> = emptyArray()
    }

    private lateinit var retrofit: Retrofit

    private val server = MockWebServer()
    private val factory = ResponseCallAdapterFactory()

    @BeforeEach
    fun setUp() {
        retrofit =
            Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(ResponseCallAdapterFactory())
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