package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.lifecycleScope
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.R
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.server.MockServer
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.server.MockServer.ERROR_RESPONSE_JSON
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.server.MockServer.SUCCESS_RESPONSE_JSON
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.service.NetworkServiceRepository
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.ui.base.BaseActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(R.layout.activity_main) {

    private lateinit var successResponseWithResultBtn: Button
    private lateinit var successResponseWithoutResultBtn: Button
    private lateinit var errorResponseBtn: Button
    private lateinit var cancelRequestBtn: Button

    private val networkServiceRepository = NetworkServiceRepository()

    private var requestJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initMockServer()
    }

    private fun initViews() {
        successResponseWithResultBtn = findViewById(R.id.success_response_with_result_btn)
        successResponseWithoutResultBtn = findViewById(R.id.success_response_without_result_btn)
        errorResponseBtn = findViewById(R.id.error_response_btn)
        cancelRequestBtn = findViewById(R.id.cancel_request_btn)
    }

    private fun initListeners() {
        successResponseWithResultBtn.setOnClickListener { successResponseWithResult() }
        successResponseWithoutResultBtn.setOnClickListener { successResponseWithoutResult() }
        errorResponseBtn.setOnClickListener { errorResponse() }
        cancelRequestBtn.setOnClickListener { cancelRequest() }
    }

    private fun initMockServer() {
        lifecycleScope.launch { MockServer.initRetrofit() }
    }

    private fun successResponseWithResult() {
        MockServer.enqueueResponse(responseCode = 200, responseBody = SUCCESS_RESPONSE_JSON)
        requestJob = networkServiceRepository.successResponseWithResult()
            .onEach { state ->
                setLoadingState(state.isLoading)
                if (state.isSuccess && !state.isEmpty) showToast(state.getData().result)
            }
            .handleErrors()
            .launchIn()
    }

    private fun successResponseWithoutResult() {
        MockServer.enqueueResponse(responseCode = 204)
        requestJob = networkServiceRepository.successResponseWithoutResult()
            .onEach { state ->
                setLoadingState(state.isLoading)
                if (state.isSuccess) showToast("Success!")
            }
            .handleErrors()
            .launchIn()
    }

    private fun errorResponse() {
        MockServer.enqueueResponse(responseCode = 500, responseBody = ERROR_RESPONSE_JSON)
        requestJob = networkServiceRepository.errorResponse()
            .onEach { state ->
                setLoadingState(state.isLoading)
                when {
                    state.isSuccess && !state.isEmpty -> showToast(state.getData().result)
                    state.isError -> showToast("See error in Logcat.")
                }
            }
            .handleErrors()
            .launchIn()
    }

    private fun cancelRequest() {
        requestJob?.apply {
            cancel()
            setLoadingState(isLoading = false)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        successResponseWithResultBtn.isEnabled = !isLoading
        successResponseWithoutResultBtn.isEnabled = !isLoading
        errorResponseBtn.isEnabled = !isLoading
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, LENGTH_SHORT).show()
    }
}