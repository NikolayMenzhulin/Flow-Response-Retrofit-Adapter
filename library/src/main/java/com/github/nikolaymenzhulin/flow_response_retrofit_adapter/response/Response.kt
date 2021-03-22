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
 * Абстрактная модель ответа от сервера.
 */
sealed class Response<out T> {

    /**
     * Загрузка данных.
     * Получение данных начинается с этого состояния.
     */
    object Loading : Response<Nothing>()

    /**
     * Успешный ответ от сервера и отсутствие каких-либо данных в нём.
     */
    object Empty : Response<Nothing>()

    /**
     * Успешный ответ от сервера с данными в нём.
     *
     * @param data данные, полученные в ответе
     */
    data class Success<T>(internal val data: T) : Response<T>()

    /**
     * Ошибка загрузки данных.
     *
     * @param error ошибка, полученная при загрузке данных
     */
    data class Error(internal val error: Throwable) : Response<Nothing>()

    /**
     * Выполняется ли запрос в данный момент?
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Выполнился ли запрос успешно?
     */
    val isSuccess: Boolean
        get() = this is Success || this is Empty

    /**
     * Выполнился ли запрос с ошибкой?
     */
    val isError: Boolean
        get() = this is Error

    /**
     * @return данные из ответа от сервера или exception, если запрос выполнился неуспешно, либо данные отсутствуют.
     */
    fun getData(): T =
        try {
            (this as Success).data
        } catch (e: ClassCastException) {
            throw IllegalStateException("Response without data")
        }

    /**
     * @return данные из ответа от сервера или null, если запрос выполнился неуспешно, либо данные отсутствуют.
     */
    fun getDataOrNull(): T? = (this as? Success)?.data

    /**
     * @return ошибка, полученная при загрузке данных или exception, если запрос выполнился успешно и ошибка отсутствует.
     */
    fun getError(): Throwable =
        try {
            (this as Error).error
        } catch (e: ClassCastException) {
            throw IllegalStateException("Response without error")
        }

    /**
     * @return ошибка, полученная при загрузке данных или null, если запрос выполнился успешно и ошибка отсутствует.
     */
    fun getErrorOrNull(): Throwable? = (this as? Error)?.error
}