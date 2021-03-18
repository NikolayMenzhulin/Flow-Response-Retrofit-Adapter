package by.nikolay_menzhulin.response_adapter_factory.adapter.call

import by.nikolay_menzhulin.response_adapter_factory.response.Response
import okhttp3.Request
import okio.Timeout
import retrofit2.Call

internal abstract class BaseResponseCall<T>(protected open val originalCall: Call<T>) : Call<Response<T>> {

    final override fun isExecuted(): Boolean = originalCall.isExecuted

    final override fun cancel(): Unit = originalCall.cancel()

    final override fun isCanceled(): Boolean = originalCall.isCanceled

    final override fun request(): Request = originalCall.request()

    final override fun timeout(): Timeout = originalCall.timeout()
}