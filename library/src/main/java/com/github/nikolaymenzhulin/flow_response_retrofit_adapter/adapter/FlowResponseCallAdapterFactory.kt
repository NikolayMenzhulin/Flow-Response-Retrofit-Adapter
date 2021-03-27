/*
  Copyright © 2021 Nikolay Menzhulin.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.adapter

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.FlowResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.Response
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowResponseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? =
        when {
            !returnType.isFlowResponse() -> {
                null
            }
            returnType !is ParameterizedType -> {
                FlowResponseCallAdapter<Nothing>(Nothing::class.java)
            }
            else -> {
                val flowResponseInnerType: Type = getParameterUpperBound(0, returnType)
                FlowResponseCallAdapter<Any?>(flowResponseInnerType)
            }
        }

    private fun Type.isFlowResponse(): Boolean = getRawType(this) == FlowResponse::class.java
}

private class FlowResponseCallAdapter<T>(private val type: Type) : CallAdapter<T, FlowResponse<T>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): FlowResponse<T> {
        val flowResponse: FlowResponse<T> = FlowResponse()
        flowResponse.responseState.tryEmit(Response.Loading)
        call.enqueue(FlowResponseCallback(flowResponse))
        return flowResponse
    }
}

private class FlowResponseCallback<T>(private val flowResponse: FlowResponse<T>) : Callback<T> {

    override fun onResponse(call: Call<T>, retrofitResponse: RetrofitResponse<T>) {
        val response: Response<T> =
            if (retrofitResponse.isSuccessful) {
                val body: T? = retrofitResponse.body()
                val isEmptyList: Boolean = (body as? List<*>)?.isEmpty() ?: false
                if (body != null && !isEmptyList) {
                    Response.Success(data = body)
                } else {
                    Response.Empty
                }
            } else {
                Response.Error(error = HttpException(retrofitResponse))
            }
        flowResponse.responseState.tryEmit(response)
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        val response: Response<T> = Response.Error(error = throwable)
        flowResponse.responseState.tryEmit(response)
    }
}

private typealias RetrofitResponse<T> = retrofit2.Response<T>