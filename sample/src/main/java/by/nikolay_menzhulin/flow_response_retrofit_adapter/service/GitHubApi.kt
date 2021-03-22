package by.nikolay_menzhulin.flow_response_retrofit_adapter.service

import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.service.response.GitHubRepoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {

    @GET("/repos/{owner}/{repo}")
    fun getRepo(
        @Path("owner") owner: String = "NikolayMenzhulin",
        @Path("repo") repo: String = "Retrofit-Response-Adapter"
    ): FlowResponse<GitHubRepoResponse>
}