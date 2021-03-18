package by.nikolay_menzhulin.sample

import by.nikolay_menzhulin.response_adapter_factory.adapter.ResponseCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHolder {

    val retrofit: Retrofit by lazy { initRetrofit() }

    private fun initRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(ResponseCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}