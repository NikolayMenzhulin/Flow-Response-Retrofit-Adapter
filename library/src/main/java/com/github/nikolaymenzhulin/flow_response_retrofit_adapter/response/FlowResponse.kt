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
package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.take

/**
 * [SharedFlow] wrapper for [Response].
 *
 * @param responseState [MutableSharedFlow] with response state
 */
data class FlowResponse<T>(
    internal val responseState: MutableSharedFlow<Response<T>> = MutableSharedFlow(replay = 2),
) : SharedFlow<Response<T>> by responseState {

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<Response<T>>) {
        responseState.take(2).collect(collector)
    }
}

/**
 * Alias for requests that not contain data or contain, but they isn't necessary and might be ignored.
 */
typealias FlowEmptyResponse = FlowResponse<Nothing>