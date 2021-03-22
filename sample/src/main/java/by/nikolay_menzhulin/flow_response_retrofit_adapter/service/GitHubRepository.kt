package by.nikolay_menzhulin.flow_response_retrofit_adapter.service

import by.nikolay_menzhulin.flow_response_retrofit_adapter.RetrofitHolder
import by.nikolay_menzhulin.flow_response_retrofit_adapter.response.FlowResponse
import by.nikolay_menzhulin.flow_response_retrofit_adapter.service.response.GitHubRepoResponse
import retrofit2.create

class GitHubRepository(
    private val gitHubApi: GitHubApi = RetrofitHolder.retrofit.create()
) {

    fun getRepo(): FlowResponse<GitHubRepoResponse> = gitHubApi.getRepo()
}