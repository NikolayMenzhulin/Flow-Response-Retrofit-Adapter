package by.nikolay_menzhulin.sample.service

import by.nikolay_menzhulin.response_adapter_factory.response.FlowResponse
import by.nikolay_menzhulin.sample.service.response.GitHubRepoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {

    @GET("/repos/{owner}/{repo}")
    fun getRepo(
        @Path("owner") owner: String = "NikolayMenzhulin",
        @Path("repo") repo: String = "Retrofit-Response-Adapter"
    ): FlowResponse<GitHubRepoResponse>
}