package by.nikolay_menzhulin.sample.service

import by.nikolay_menzhulin.response_adapter_factory.response.Response
import by.nikolay_menzhulin.sample.RetrofitHolder
import by.nikolay_menzhulin.sample.service.response.GitHubRepoResponse
import retrofit2.create

class GitHubRepository(
    private val gitHubApi: GitHubApi = RetrofitHolder.retrofit.create()
) {

    suspend fun getRepo(): Response<GitHubRepoResponse> = gitHubApi.getRepo()
}