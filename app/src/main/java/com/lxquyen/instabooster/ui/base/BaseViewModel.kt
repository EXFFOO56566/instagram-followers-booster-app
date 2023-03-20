package com.lxquyen.instabooster.ui.base

import com.lxquyen.instabooster.BuildConfig
import com.ohayo.core.ui.base.CoreViewModel
import kotlinx.coroutines.flow.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber

/**
 * Created by Furuichi on 4/7/2022
 */
open class BaseViewModel : CoreViewModel() {
    val indicatorEvent: SharedFlow<IndicatorState>
        get() = _indicatorEvent

    private val _indicatorEvent = MutableSharedFlow<IndicatorState>(extraBufferCapacity = 1)

    fun <T> Flow<T>.transformLoading(): Flow<T> {
        return this
            .onStart {
                _indicatorEvent.emit(IndicatorState.Loading)
            }
            .catchFlow {
                Timber.e(it)
                _indicatorEvent.emit(IndicatorState.Error(throwable = it))
            }
            .onCompletion {
                _indicatorEvent.emit(IndicatorState.Idle)
            }
    }

    fun <T> Flow<T>.transformLoadingWithoutError(): Flow<T> {
        return this
            .onStart {
                _indicatorEvent.emit(IndicatorState.Loading)
            }
            .catch { }
            .onCompletion {
                _indicatorEvent.emit(IndicatorState.Idle)
            }
    }

    fun <T> Flow<T>.catchFlow(
        block: suspend ((Throwable) -> Unit) = {
            Timber.e((it))
            _indicatorEvent.tryEmit(IndicatorState.Error(throwable = it))
        }
    ): Flow<T> {
        return this.catch {
            block.invoke(it)
        }
    }

    fun <T> Flow<T>.onEachSafety(action: suspend (T) -> Unit): Flow<T> {
        return this.onEach(action)
            .catch {
                Timber.d("here-43: ${it.message}")
            }
    }
}

sealed class IndicatorState {
    object Idle : IndicatorState()

    object Loading : IndicatorState()

    class Error(val throwable: Throwable) : IndicatorState() {
        val message: String
            get() {
                return handleThrowable()
            }

        private fun handleThrowable(): String {
            return when (throwable) {
                is HttpException -> parseHttpException(throwable)
                else -> throwable.message ?: "Service unavailable"
            }
        }

        private fun parseHttpException(httpException: HttpException): String {
            if (!BuildConfig.DEBUG) {
                return httpException.message()
            }

            return try {

                val errorBody = httpException.response()?.errorBody()?.string() ?: ""
                val errorJson = JSONObject(errorBody)
                errorJson.getString("error")
            } catch (ex: JSONException) {
                "Service unavailable"
            }
        }
    }
}