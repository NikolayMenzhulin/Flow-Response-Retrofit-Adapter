package by.nikolay_menzhulin.response_adapter_factory.network.response

import com.google.gson.annotations.SerializedName

data class TestResponse(
    @SerializedName("mockString") val mockString: String,
    @SerializedName("mockInt") val mockInt: Int,
    @SerializedName("mockBoolean") val mockBoolean: Boolean
)