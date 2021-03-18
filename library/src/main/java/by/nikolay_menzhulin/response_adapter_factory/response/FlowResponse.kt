package by.nikolay_menzhulin.response_adapter_factory.response

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * StateFlow-обёртка над состоянием ответа от сервера.
 *
 * @property responseState изменяемый StateFlow с состоянием ответа от сервера.
 */
data class FlowResponse<T>(
    internal val responseState: MutableStateFlow<Response<T>> = MutableStateFlow(Response.Loading),
) : StateFlow<Response<T>> by responseState

/**
 * Псевдоним для использования с запросами, ответ на которые не содержит данных,
 * либо данные есть, но не требуются и их можно проигнорировать.
 */
typealias FlowEmptyResponse = FlowResponse<Nothing>