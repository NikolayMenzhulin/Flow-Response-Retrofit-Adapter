package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowEmptyResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network.response.NetworkServiceResponse
import retrofit2.http.GET

interface NetworkServiceApi {

    @GET("/")
    fun successResponseWithResult(): FlowResponse<NetworkServiceResponse>

    @GET("/")
    fun successResponseWithoutResult(): FlowEmptyResponse

    @GET("/")
    fun errorResponse(): FlowResponse<NetworkServiceResponse>
}