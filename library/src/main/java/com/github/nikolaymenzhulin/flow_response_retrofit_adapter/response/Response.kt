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

/**
 * Abstract model of response from some data source.
 */
sealed class Response<out T> {

    /**
     * Data loading.
     * Data receiving starts from this state.
     */
    object Loading : Response<Nothing>()

    /**
     * Successful response from data source without data.
     */
    object Empty : Response<Nothing>()

    /**
     * Successful response from data source with data.
     *
     * @param data data from response
     */
    data class Success<T>(internal val data: T) : Response<T>()

    /**
     * Data loading error.
     *
     * @param error error receiving if request is failure
     */
    data class Error(internal val error: Throwable) : Response<Nothing>()

    /**
     * Was request started?
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Is response data empty?
     */
    val isEmpty: Boolean
        get() = this is Empty

    /**
     * Was request successful?
     */
    val isSuccess: Boolean
        get() = this is Success || this is Empty

    /**
     * Was request ended with error?
     */
    val isError: Boolean
        get() = this is Error

    /**
     * @return data from response or exception if request is failure or data is empty
     */
    fun getData(): T =
        try {
            (this as Success).data
        } catch (e: ClassCastException) {
            throw IllegalStateException("Response without data")
        }

    /**
     * @return data from response or null if request is failure or data is empty
     */
    fun getDataOrNull(): T? = (this as? Success)?.data

    /**
     * @return error received while data loading or exception if request is successful
     */
    fun getError(): Throwable =
        try {
            (this as Error).error
        } catch (e: ClassCastException) {
            throw IllegalStateException("Response without error")
        }

    /**
     * @return error received while data loading or null if request is successful
     */
    fun getErrorOrNull(): Throwable? = (this as? Error)?.error
}