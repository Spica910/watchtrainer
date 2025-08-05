package com.watchtrainer.utils

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 앱 성능 모니터링을 위한 유틸리티 클래스
 * 메모리 사용량, 프레임 레이트 등을 추적합니다.
 */
object PerformanceMonitor {
    private const val TAG = "PerformanceMonitor"
    
    private val _memoryUsage = MutableStateFlow(0L)
    val memoryUsage: StateFlow<Long> = _memoryUsage.asStateFlow()
    
    private val _frameDropCount = MutableStateFlow(0)
    val frameDropCount: StateFlow<Int> = _frameDropCount.asStateFlow()
    
    fun logMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val percentUsed = (usedMemory * 100) / maxMemory
        
        _memoryUsage.value = usedMemory
        
        if (percentUsed > 80) {
            Log.w(TAG, "High memory usage: $percentUsed%")
        }
        
        Log.d(TAG, "Memory usage: ${usedMemory / 1024 / 1024}MB / ${maxMemory / 1024 / 1024}MB ($percentUsed%)")
    }
    
    fun reportFrameDrop() {
        _frameDropCount.value++
        Log.w(TAG, "Frame drop detected. Total drops: ${_frameDropCount.value}")
    }
    
    fun reset() {
        _memoryUsage.value = 0L
        _frameDropCount.value = 0
    }
}