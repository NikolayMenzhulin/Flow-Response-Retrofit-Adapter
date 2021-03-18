package by.nikolay_menzhulin.sample.service

import by.nikolay_menzhulin.response_adapter_factory.response.FlowResponse
import by.nikolay_menzhulin.sample.RetrofitHolder
import by.nikolay_menzhulin.sample.service.response.GitHubRepoResponse
import retrofit2.create

class GitHubRepository(
    private val gitHubApi: GitHubApi = RetrofitHolder.retrofit.create()
) {

    fun getRepo(): FlowResponse<GitHubRepoResponse> = gitHubApi.getRepo()
}