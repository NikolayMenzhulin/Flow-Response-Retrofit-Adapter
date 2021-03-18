package by.nikolay_menzhulin.sample.service.response

import com.google.gson.annotations.SerializedName

data class GitHubRepoResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("node_id") val nodeId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("private") val isPrivate: Boolean? = null
)