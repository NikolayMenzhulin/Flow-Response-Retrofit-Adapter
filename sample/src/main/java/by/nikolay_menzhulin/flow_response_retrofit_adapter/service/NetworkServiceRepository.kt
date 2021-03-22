package by.nikolay_menzhulin.flow_response_retrofit_adapter.service

import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowEmptyResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.server.MockServer
import by.nikolay_menzhulin.flow_response_retrofit_adapter.service.network.NetworkServiceApi
import by.nikolay_menzhulin.flow_response_retrofit_adapter.service.network.response.NetworkServiceResponse
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