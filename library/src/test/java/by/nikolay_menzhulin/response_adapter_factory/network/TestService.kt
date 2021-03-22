package by.nikolay_menzhulin.response_adapter_factory.network

import by.nikolay_menzhulin.response_adapter_factory.network.response.TestResponse
import by.nikolay_menzhulin.response_adapter_factory.response.FlowEmptyResponse
import by.nikolay_menzhulin.response_adapter_factory.response.FlowResponse
import retrofit2.http.GET

interface TestService {

    @GET("/")
    fun responseWithData(): FlowResponse<TestResponse>

    @GET("/")
    fun responseWithoutData(): FlowEmptyResponse
}