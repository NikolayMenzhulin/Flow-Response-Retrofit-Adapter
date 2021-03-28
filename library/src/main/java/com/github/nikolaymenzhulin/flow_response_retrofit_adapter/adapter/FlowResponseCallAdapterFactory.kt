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

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowResponseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? =
        when {
            !returnType.isFlowWithResponseInside() -> {
                null
            }
            !returnType.isResponseParametrized() -> {
                FlowResponseCallAdapter<Nothing>(Nothing::class.java)
            }
            else -> {
                val flowResponseInnerType: Type = returnType.getResponseInnerType()
                FlowResponseCallAdapter<Any?>(flowResponseInnerType)
            }
        }

    private fun Type.isFlowWithResponseInside(): Boolean =
        if (getRawType(this) == Flow::class.java && this is ParameterizedType) {
            val flowInnerType: Type = getParameterUpperBound(0, this)
            getRawType(flowInnerType) == Response::class.java
        } else {
            false
        }

    private fun Type.isResponseParametrized(): Boolean =
        getParameterUpperBound(0, this as ParameterizedType) is ParameterizedType

    private fun Type.getResponseInnerType(): Type =
        getParameterUpperBound(0, getParameterUpperBound(0, this as ParameterizedType) as ParameterizedType)
}

private class FlowResponseCallAdapter<T>(private val type: Type) : CallAdapter<T, Flow<Response<T>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Flow<Response<T>> {
        val responseState: MutableSharedFlow<Response<T>> = MutableSharedFlow(replay = 2)
        responseState.tryEmit(Response.Loading)
        call.enqueue(FlowResponseCallback(responseState))
        return responseState.take(2)
    }
}

private class FlowResponseCallback<T>(private val responseState: MutableSharedFlow<Response<T>>) : Callback<T> {

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
        responseState.tryEmit(response)
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        val response: Response<T> = Response.Error(error = throwable)
        responseState.tryEmit(response)
    }
}

private typealias RetrofitResponse<T> = retrofit2.Response<T>