package by.nikolay_menzhulin.adapter.call

import by.nikolay_menzhulin.response.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException

internal class ResponseCall<T>(override val originalCall: Call<T>) : BaseResponseCall<T>(originalCall) {

    override fun execute(): RetrofitResponse<Response<T>> =
        try {
            onResponse(originalCall.execute())
        } catch (e: Exception) {
            onFailure(e)
        }

    override fun enqueue(callback: Callback<Response<T>>): Unit =
        originalCall.enqueue(ResponseCallback(responseCall = this, originalCallback = callback))

    override fun clone(): ResponseCall<T> = ResponseCall(originalCall.clone())

    private fun onResponse(retrofitResponse: RetrofitResponse<T>): RetrofitResponse<Response<T>> {
        val response: Response<T> =
            if (retrofitResponse.isSuccessful) {
                retrofitResponse.body()?.let {
                    Response.Success(
                        data = it
                    )
                } ?: Response.Empty
            } else {
                Response.Error(
                    error = HttpException(retrofitResponse)
                )
            }
        return RetrofitResponse.success(response)
    }

    private fun onFailure(throwable: Throwable): RetrofitResponse<Response<T>> {
        val response: Response<T> =
            Response.Error(
                error = throwable
            )
        return RetrofitResponse.success(response)
    }

    private inner class ResponseCallback(
        private val responseCall: ResponseCall<T>,
        private val originalCallback: Callback<Response<T>>
    ) : Callback<T> {

        override fun onResponse(call: Call<T>, retrofitResponse: RetrofitResponse<T>) {
            originalCallback.onResponse(responseCall, onResponse(retrofitResponse))
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
            originalCallback.onResponse(responseCall, onFailure(throwable))
        }
    }
}

private typealias RetrofitResponse<T> = retrofit2.Response<T>