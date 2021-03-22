package com.github.nikolaymenzhulin.flow_response_retrofit_adapter.ui.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.ui.base.BaseActivity.LaunchType.*
import com.github.nikolaymenzhulin.flow_response_retrofit_adapter.ui.base.error.ErrorHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn

abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId), ErrorHandler {

    enum class LaunchType {
        STANDARD, ON_CREATE, ON_START, ON_RESUME
    }

    fun <T> Flow<T>.launchIn(launchType: LaunchType = ON_START): Job =
        with(lifecycleScope) {
            when (launchType) {
                STANDARD -> launchIn(lifecycleScope)
                ON_CREATE -> launchWhenCreated { collect() }
                ON_START -> launchWhenStarted { collect() }
                ON_RESUME -> launchWhenResumed { collect() }
            }
        }
}