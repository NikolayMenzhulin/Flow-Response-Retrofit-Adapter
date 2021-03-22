package by.nikolay_menzhulin.flow_response_retrofit_adapter.adapter

import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.Response
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResponseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? =
        when {
            !returnType.isFlowResponse() -> {
                null
            }
            returnType !is ParameterizedType -> {
                ResponseCallAdapter<Nothing>(Nothing::class.java)
            }
            else -> {
                val flowResponseInnerType: Type = getParameterUpperBound(0, returnType)
                ResponseCallAdapter<Any?>(flowResponseInnerType)
            }
        }

    private fun Type.isFlowResponse(): Boolean = getRawType(this) == FlowResponse::class.java
}

private class ResponseCallAdapter<T>(private val type: Type) : CallAdapter<T, FlowResponse<T>> {

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
                retrofitResponse.body()?.let { Response.Success(data = it) } ?: Response.Empty
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