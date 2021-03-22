package by.nikolay_menzhulin.response_adapter_factory.response

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.take

/**
 * SharedFlow-обёртка над состоянием ответа от сервера.
 *
 * @property responseState изменяемый SharedFlow с состоянием ответа от сервера.
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
 * Псевдоним для использования с запросами, ответ на которые не содержит данных,
 * либо данные есть, но не требуются и их можно проигнорировать.
 */
typealias FlowEmptyResponse = FlowResponse<Nothing>