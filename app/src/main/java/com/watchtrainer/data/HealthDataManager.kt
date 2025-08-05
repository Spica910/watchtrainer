package com.watchtrainer.data

import android.content.Context
import android.util.Log
import com.samsung.android.sdk.healthdata.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class HealthDataManager(private val context: Context) {
    
    companion object {
        private const val TAG = "HealthDataManager"
        private const val APP_TAG = "WatchTrainer"
    }
    
    private var healthDataStore: HealthDataStore? = null
    private var healthDataService: HealthDataService? = null
    
    private val connectionListener = object : HealthDataStore.ConnectionListener {
        override fun onConnected() {
            Log.d(TAG, "Health data service connected")
            healthDataService = HealthDataService(healthDataStore!!)
        }
        
        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            Log.e(TAG, "Health data service connection failed: ${error.errorCode}")
        }
        
        override fun onDisconnected() {
            Log.d(TAG, "Health data service disconnected")
            healthDataService = null
        }
    }
    
    fun initialize() {
        try {
            healthDataStore = HealthDataStore(context, connectionListener)
            healthDataStore?.connectService()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize health data store", e)
        }
    }
    
    fun disconnect() {
        healthDataStore?.disconnectService()
        healthDataStore = null
        healthDataService = null
    }
    
    fun observeHealthData(): Flow<HealthData> = callbackFlow {
        val healthData = HealthData()
        
        // Subscribe to step count updates
        val stepCountListener = object : HealthResultHolder.ResultListener<HealthDataObserver> {
            override fun onResult(result: HealthDataObserver) {
                val count = result.getCount()
                if (count > 0) {
                    result.setDataReader(HealthDataReader(healthDataStore!!))
                    try {
                        while (result.hasNext()) {
                            val data = result.next()
                            val steps = data.getInt(HealthConstants.StepCount.COUNT)
                            trySend(healthData.copy(steps = steps))
                        }
                    } finally {
                        result.close()
                    }
                }
            }
        }
        
        // Subscribe to heart rate updates
        val heartRateListener = object : HealthResultHolder.ResultListener<HealthDataObserver> {
            override fun onResult(result: HealthDataObserver) {
                val count = result.getCount()
                if (count > 0) {
                    result.setDataReader(HealthDataReader(healthDataStore!!))
                    try {
                        while (result.hasNext()) {
                            val data = result.next()
                            val heartRate = data.getInt(HealthConstants.HeartRate.HEART_RATE)
                            trySend(healthData.copy(heartRate = heartRate))
                        }
                    } finally {
                        result.close()
                    }
                }
            }
        }
        
        // Set up observers
        healthDataService?.let { service ->
            val stepCountObserver = HealthDataObserver.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .build()
            
            val heartRateObserver = HealthDataObserver.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .build()
            
            service.registerObserver(stepCountObserver, stepCountListener)
            service.registerObserver(heartRateObserver, heartRateListener)
        }
        
        awaitClose {
            // Clean up observers
        }
    }
    
    suspend fun getTodaySteps(): Int {
        return try {
            val resolver = HealthDataResolver(healthDataStore, null)
            val startTime = getStartOfDay()
            val endTime = System.currentTimeMillis()
            
            val request = HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .setProperties(arrayOf(HealthConstants.StepCount.COUNT))
                .setLocalTimeRange(
                    HealthConstants.StepCount.START_TIME,
                    HealthConstants.StepCount.END_TIME,
                    startTime,
                    endTime
                )
                .build()
            
            val result = resolver.read(request).await()
            var totalSteps = 0
            
            try {
                while (result.hasNext()) {
                    val data = result.next()
                    totalSteps += data.getInt(HealthConstants.StepCount.COUNT)
                }
            } finally {
                result.close()
            }
            
            totalSteps
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get today's steps", e)
            0
        }
    }
    
    suspend fun getLatestHeartRate(): Int {
        return try {
            val resolver = HealthDataResolver(healthDataStore, null)
            
            val request = HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(arrayOf(HealthConstants.HeartRate.HEART_RATE))
                .setSortOrder(HealthDataResolver.SortOrder.DESC)
                .setLimit(1)
                .build()
            
            val result = resolver.read(request).await()
            var heartRate = 0
            
            try {
                if (result.hasNext()) {
                    val data = result.next()
                    heartRate = data.getInt(HealthConstants.HeartRate.HEART_RATE)
                }
            } finally {
                result.close()
            }
            
            heartRate
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get heart rate", e)
            0
        }
    }
    
    suspend fun getTodayCalories(): Float {
        return try {
            val resolver = HealthDataResolver(healthDataStore, null)
            val startTime = getStartOfDay()
            val endTime = System.currentTimeMillis()
            
            val request = HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Exercise.HEALTH_DATA_TYPE)
                .setProperties(arrayOf(HealthConstants.Exercise.CALORIE))
                .setLocalTimeRange(
                    HealthConstants.Exercise.START_TIME,
                    HealthConstants.Exercise.END_TIME,
                    startTime,
                    endTime
                )
                .build()
            
            val result = resolver.read(request).await()
            var totalCalories = 0f
            
            try {
                while (result.hasNext()) {
                    val data = result.next()
                    totalCalories += data.getFloat(HealthConstants.Exercise.CALORIE)
                }
            } finally {
                result.close()
            }
            
            totalCalories
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get today's calories", e)
            0f
        }
    }
    
    private fun getStartOfDay(): Long {
        val now = System.currentTimeMillis()
        return now - (now % TimeUnit.DAYS.toMillis(1))
    }
}

// Extension function for coroutine support
suspend fun <T> HealthResultHolder<T>.await(): T {
    return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        setResultListener { result ->
            continuation.resumeWith(Result.success(result))
        }
    }
}