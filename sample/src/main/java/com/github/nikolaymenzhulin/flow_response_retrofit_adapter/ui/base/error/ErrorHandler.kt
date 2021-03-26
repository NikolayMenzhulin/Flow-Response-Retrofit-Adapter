package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.ui.base.error

import android.util.Log
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.response.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

interface ErrorHandler {

    fun <T> Flow<Response<T>>.handleErrors(): Flow<Response<T>> = onEach(::handleErrorIfHas)

    private fun handleErrorIfHas(state: Response<*>) {
        if (state.isError) {
            Log.e("TEST_TAG", state.getError().stackTraceToString())
        }
    }
}