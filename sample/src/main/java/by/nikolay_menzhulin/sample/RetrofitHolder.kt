package by.nikolay_menzhulin.sample

import by.nikolay_menzhulin.response_adapter_factory.adapter.ResponseCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHolder {

    val retrofit: Retrofit by lazy { initRetrofit() }

    private fun initRetrofit(): Retrofit =
        Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(ResponseCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}