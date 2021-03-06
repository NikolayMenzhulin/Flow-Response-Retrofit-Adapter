/*
  Copyright © 2021 Nikolay Menzhulin.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.network.api

import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.network.response.TestResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.typealiases.FlowResponse
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.typealiases.FlowResponseEmpty
import okhttp3.ResponseBody
import retrofit2.http.GET

interface TestApi {

    @GET("/")
    fun responseWithResponseBody(): FlowResponse<ResponseBody>

    @GET("/")
    fun responseWithData(): FlowResponse<TestResponse>

    @GET("/")
    fun responseWithDataList(): FlowResponse<List<TestResponse>>

    @GET("/")
    fun responseWithoutData(): FlowResponseEmpty
}