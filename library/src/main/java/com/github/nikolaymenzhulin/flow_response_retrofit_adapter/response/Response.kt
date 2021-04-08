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
 * The abstract model of a response from a data source.
 */
sealed class Response<out T> {

    /**
     * The data loading state.
     * Data receiving starts from this state.
     */
    object Loading : Response<Nothing>()

    /**
     * The successful response state from a data source without data.
     */
    object Empty : Response<Nothing>()

    /**
     * The successful response state from a data source with data.
     *
     * @param data the data from the response
     */
    data class Success<T>(internal val data: T) : Response<T>()

    /**
     * The data loading error.
     *
     * @param error the error receiving if the request is failure
     */
    data class Error(internal val error: Throwable) : Response<Nothing>()

    /**
     * Was the request started?
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Is the response data empty?
     */
    val isEmpty: Boolean
        get() = this is Empty

    /**
     * Was the request successful?
     */
    val isSuccess: Boolean
        get() = this is Success || this is Empty

    /**
     * Was the request ended with an error?
     */
    val isError: Boolean
        get() = this is Error

    /**
     * @return the data from the response or an error if the request is failure or the data is empty
     */
    fun getData(): T =
        try {
            (this as Success).data
        } catch (e: ClassCastException) {
            throw IllegalStateException("The response without data")
        }

    /**
     * @return the data from the response or null if the request is failure or the data is empty
     */
    fun getDataOrNull(): T? = (this as? Success)?.data

    /**
     * @return the error received while the data loading or an exception if the request is successful
     */
    fun getError(): Throwable =
        try {
            (this as Error).error
        } catch (e: ClassCastException) {
            throw IllegalStateException("The response without an error")
        }

    /**
     * @return the error received while the data loading or null if the request is successful
     */
    fun getErrorOrNull(): Throwable? = (this as? Error)?.error
}