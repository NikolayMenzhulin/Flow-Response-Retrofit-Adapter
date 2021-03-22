package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowEmptyResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.server.MockServer
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network.NetworkServiceApi
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network.response.NetworkServiceResponse
import retrofit2.create

class NetworkServiceRepository {

    private val networkServiceApi: NetworkServiceApi by lazy { MockServer.retrofit.create() }

    fun successResponseWithResult(): FlowResponse<NetworkServiceResponse> =
        networkServiceApi.successResponseWithResult()

    fun successResponseWithoutResult(): FlowEmptyResponse =
        networkServiceApi.successResponseWithoutResult()

    fun errorResponse(): FlowResponse<NetworkServiceResponse> =
        networkServiceApi.errorResponse()
}