package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.network.response

import com.google.gson.annotations.SerializedName

data class NetworkServiceResponse(@SerializedName("result") val result: String)