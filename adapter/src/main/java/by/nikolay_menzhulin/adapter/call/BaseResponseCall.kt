package by.nikolay_menzhulin.adapter.call

import by.nikolay_menzhulin.response.Response
import okhttp3.Request
import okio.Timeout
import retrofit2.Call

internal abstract class BaseResponseCall<T>(protected open val originalCall: Call<T>) : Call<Response<T>> {

    override fun isExecuted(): Boolean = originalCall.isExecuted

    override fun cancel(): Unit = originalCall.cancel()

    override fun isCanceled(): Boolean = originalCall.isCanceled

    override fun request(): Request = originalCall.request()

    override fun timeout(): Timeout = originalCall.timeout()
}