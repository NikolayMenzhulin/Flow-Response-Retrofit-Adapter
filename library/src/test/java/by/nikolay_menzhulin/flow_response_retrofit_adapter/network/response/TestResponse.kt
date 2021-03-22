package by.nikolay_menzhulin.flow_response_retrofit_adapter.network.response

import com.google.gson.annotations.SerializedName

data class TestResponse(
    @SerializedName("mockString") val mockString: String,
    @SerializedName("mockInt") val mockInt: Int,
    @SerializedName("mockBoolean") val mockBoolean: Boolean
)