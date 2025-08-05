package com.watchtrainer.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 앱 전체의 에러 처리를 담당하는 유틸리티 클래스
 * 사용자 친화적인 에러 메시지를 제공합니다.
 */
object ErrorHandler {
    private const val TAG = "ErrorHandler"
    
    private val _errorEvents = MutableSharedFlow<ErrorEvent>()
    val errorEvents: SharedFlow<ErrorEvent> = _errorEvents.asSharedFlow()
    
    /**
     * 에러를 처리하고 사용자에게 적절한 메시지를 표시합니다.
     */
    fun handleError(
        context: Context,
        error: Throwable,
        userMessage: String? = null,
        showToast: Boolean = true
    ) {
        Log.e(TAG, "Error occurred", error)
        
        val message = userMessage ?: when (error) {
            is NetworkException -> "네트워크 연결을 확인해주세요"
            is ApiException -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요"
            is DataException -> "데이터 처리 중 오류가 발생했습니다"
            is PermissionException -> "필요한 권한이 없습니다"
            else -> "오류가 발생했습니다"
        }
        
        if (showToast) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        
        _errorEvents.tryEmit(ErrorEvent(error, message))
    }
    
    /**
     * Coroutine에서 발생하는 예외를 처리하는 핸들러
     */
    fun getCoroutineExceptionHandler(context: Context) = CoroutineExceptionHandler { _, exception ->
        handleError(context, exception)
    }
    
    /**
     * 에러 타입에 따른 재시도 가능 여부 확인
     */
    fun isRetryable(error: Throwable): Boolean {
        return when (error) {
            is NetworkException -> true
            is ApiException -> error.code in 500..599
            else -> false
        }
    }
}

/**
 * 에러 이벤트 데이터 클래스
 */
data class ErrorEvent(
    val error: Throwable,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 커스텀 예외 클래스들
 */
class NetworkException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class ApiException(val code: Int, message: String? = null) : Exception(message)
class DataException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class PermissionException(val permission: String, message: String? = null) : Exception(message)