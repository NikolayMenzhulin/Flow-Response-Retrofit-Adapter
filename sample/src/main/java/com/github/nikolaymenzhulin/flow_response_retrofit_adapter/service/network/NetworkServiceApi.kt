package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network.response.NetworkServiceResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.typealiases.FlowResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.typealiases.FlowResponseEmpty
import retrofit2.http.GET

interface NetworkServiceApi {

    @GET("/")
    fun successResponseWithResult(): FlowResponse<NetworkServiceResponse>

    @GET("/")
    fun successResponseWithoutResult(): FlowResponseEmpty

    @GET("/")
    fun errorResponse(): FlowResponse<NetworkServiceResponse>
}