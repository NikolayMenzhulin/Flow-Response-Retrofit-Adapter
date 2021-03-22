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

    private var job: Job? = null

    private fun successResponseWithResult() {
        MockServer.enqueueResponse(responseCode = 200, responseBody = SUCCESS_RESPONSE_JSON)
        job = networkServiceRepository.successResponseWithResult()
            .handleErrors()
            .onEach { state ->
                setLoadingState(state.isLoading)
                when {
                    state.isLoading -> return@onEach
                    state.isSuccess -> Toast.makeText(this, state.getData().result, LENGTH_SHORT).show()
                }
            }.launchIn()
    }

    private fun successResponseWithoutResult() {
        MockServer.enqueueResponse(responseCode = 204)
        job = networkServiceRepository.successResponseWithoutResult()
            .handleErrors()
            .onEach { state ->
                setLoadingState(state.isLoading)
                when {
                    state.isLoading -> return@onEach
                    state.isSuccess -> Toast.makeText(this, "Success!", LENGTH_SHORT).show()
                }
            }.launchIn()
    }

    private fun errorResponse() {
        MockServer.enqueueResponse(responseCode = 500, responseBody = ERROR_RESPONSE_JSON)
        job = networkServiceRepository.errorResponse()
            .handleErrors()
            .onEach { state ->
                setLoadingState(state.isLoading)
                when {
                    state.isLoading -> return@onEach
                    state.isSuccess -> Toast.makeText(this, state.getData().result, LENGTH_SHORT).show()
                    state.isError -> Toast.makeText(this, "See error in Logcat.", LENGTH_SHORT).show()
                }
            }.launchIn()
    }

    private fun cancelRequest() {
        job?.apply {
            cancel()
            setLoadingState(isLoading = false)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        successResponseWithResultBtn.isEnabled = !isLoading
        successResponseWithoutResultBtn.isEnabled = !isLoading
        errorResponseBtn.isEnabled = !isLoading
    }
}