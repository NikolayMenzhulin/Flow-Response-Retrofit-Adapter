package by.nikolay_menzhulin.adapter

import by.nikolay_menzhulin.adapter.call.ResponseCall
import by.nikolay_menzhulin.response.Response
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResponseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val innerType: Type = returnType.getInnerTypeIfHas() ?: return null
        if (!innerType.isResponse()) return null

        return if (innerType is ParameterizedType) {
            val responseInnerType: Type = getParameterUpperBound(0, innerType)
            ResponseCallAdapter<Any?>(responseInnerType)
        } else {
            ResponseCallAdapter<Nothing>(Nothing::class.java)
        }
    }

    private fun Type.getInnerTypeIfHas(): Type? =
        if (getRawType(this) == Call::class.java && this is ParameterizedType) {
            getParameterUpperBound(0, this)
        } else {
            null
        }

    private fun Type.isResponse(): Boolean = getRawType(this) == Response::class.java
}

private class ResponseCallAdapter<T>(private val type: Type) : CallAdapter<T, Call<Response<T>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Call<Response<T>> = ResponseCall(call)
}