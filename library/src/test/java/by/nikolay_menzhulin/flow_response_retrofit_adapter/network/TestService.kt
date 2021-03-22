package by.nikolay_menzhulin.flow_response_retrofit_adapter.network

import by.nikolay_menzhulin.flow_response_retrofit_adapter.network.response.TestResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowEmptyResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowResponse
import retrofit2.http.GET

interface TestService {

    @GET("/")
    fun responseWithData(): FlowResponse<TestResponse>

    @GET("/")
    fun responseWithoutData(): FlowEmptyResponse
}