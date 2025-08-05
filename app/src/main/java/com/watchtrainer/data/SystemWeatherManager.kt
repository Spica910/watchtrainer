package com.watchtrainer.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Wear OS 시스템의 날씨 정보를 활용하는 매니저
 * Samsung Galaxy Watch는 Galaxy Wearable 앱과 연동되어 날씨 정보를 제공합니다.
 */
class SystemWeatherManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SystemWeatherManager"
        
        // Samsung Weather Provider
        private const val SAMSUNG_WEATHER_PACKAGE = "com.samsung.android.weather"
        private const val SAMSUNG_WEATHER_PROVIDER = "com.samsung.android.weather.provider"
        
        // Google Weather (Wear OS)
        private const val GOOGLE_WEATHER_PACKAGE = "com.google.android.googlequicksearchbox"
    }
    
    /**
     * Wear OS 시스템에서 날씨 정보를 가져옵니다.
     * 1. Samsung Weather (Galaxy Watch)
     * 2. Google Weather (Wear OS)
     * 3. 실패 시 WeatherDataManager로 폴백
     */
    suspend fun getSystemWeatherData(): WeatherData? = withContext(Dispatchers.IO) {
        try {
            // 1. Try Samsung Weather first (for Galaxy Watch)
            getSamsungWeatherData()?.let { return@withContext it }
            
            // 2. Try Google Weather
            getGoogleWeatherData()?.let { return@withContext it }
            
            // 3. Check if any weather complication is available
            getComplicationWeatherData()?.let { return@withContext it }
            
            Log.w(TAG, "No system weather data available")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting system weather data", e)
            null
        }
    }
    
    private fun getSamsungWeatherData(): WeatherData? {
        try {
            // Check if Samsung Weather is installed
            if (!isPackageInstalled(SAMSUNG_WEATHER_PACKAGE)) {
                return null
            }
            
            // Samsung Weather stores data in shared preferences that can be accessed
            // through content provider or broadcast receiver
            val weatherIntent = Intent("com.samsung.android.weather.WEATHER_UPDATE")
            weatherIntent.setPackage(SAMSUNG_WEATHER_PACKAGE)
            
            // Try to get weather data from Samsung Weather content provider
            val uri = android.net.Uri.parse("content://$SAMSUNG_WEATHER_PROVIDER/weather")
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val tempIndex = it.getColumnIndex("temperature")
                    val descIndex = it.getColumnIndex("description")
                    val humidityIndex = it.getColumnIndex("humidity")
                    val windIndex = it.getColumnIndex("wind_speed")
                    
                    if (tempIndex >= 0) {
                        return WeatherData(
                            temperature = it.getFloat(tempIndex),
                            description = if (descIndex >= 0) it.getString(descIndex) ?: "" else "",
                            humidity = if (humidityIndex >= 0) it.getInt(humidityIndex) else 0,
                            windSpeed = if (windIndex >= 0) it.getFloat(windIndex) else 0f,
                            feelsLike = it.getFloat(tempIndex), // Simplified
                            condition = WeatherCondition.CLEAR // Parse from description
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Samsung Weather not available: ${e.message}")
        }
        return null
    }
    
    private fun getGoogleWeatherData(): WeatherData? {
        try {
            // Google Assistant/Search provides weather through complications
            if (!isPackageInstalled(GOOGLE_WEATHER_PACKAGE)) {
                return null
            }
            
            // Try to access Google Weather through content provider
            val uri = android.net.Uri.parse("content://com.google.android.apps.weather.provider/current")
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            
            cursor?.use {
                if (it.moveToFirst()) {
                    // Parse Google Weather data format
                    return parseGoogleWeatherCursor(it)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Google Weather not available: ${e.message}")
        }
        return null
    }
    
    private fun getComplicationWeatherData(): WeatherData? {
        try {
            // Wear OS complications can provide weather data
            // This requires the RECEIVE_COMPLICATION_DATA permission
            val prefs = context.getSharedPreferences("weather_complication", Context.MODE_PRIVATE)
            val weatherJson = prefs.getString("latest_weather", null) ?: return null
            
            val json = JSONObject(weatherJson)
            return WeatherData(
                temperature = json.optDouble("temperature", 0.0).toFloat(),
                description = json.optString("description", ""),
                humidity = json.optInt("humidity", 0),
                windSpeed = json.optDouble("windSpeed", 0.0).toFloat(),
                feelsLike = json.optDouble("feelsLike", 0.0).toFloat(),
                condition = parseCondition(json.optString("condition", ""))
            )
        } catch (e: Exception) {
            Log.d(TAG, "No complication weather data: ${e.message}")
        }
        return null
    }
    
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    private fun parseGoogleWeatherCursor(cursor: android.database.Cursor): WeatherData? {
        // Parse Google's weather data format
        // Column names may vary, so we try common ones
        val possibleTempColumns = listOf("temperature", "temp", "current_temp")
        val possibleDescColumns = listOf("description", "condition", "weather")
        
        var temperature = 0f
        var description = ""
        
        for (col in possibleTempColumns) {
            val index = cursor.getColumnIndex(col)
            if (index >= 0) {
                temperature = cursor.getFloat(index)
                break
            }
        }
        
        for (col in possibleDescColumns) {
            val index = cursor.getColumnIndex(col)
            if (index >= 0) {
                description = cursor.getString(index) ?: ""
                break
            }
        }
        
        return if (temperature != 0f) {
            WeatherData(
                temperature = temperature,
                description = description,
                humidity = 0,
                windSpeed = 0f,
                feelsLike = temperature,
                condition = parseCondition(description)
            )
        } else null
    }
    
    private fun parseCondition(description: String): WeatherCondition {
        val desc = description.lowercase()
        return when {
            desc.contains("clear") || desc.contains("sunny") -> WeatherCondition.CLEAR
            desc.contains("cloud") -> WeatherCondition.CLOUDY
            desc.contains("rain") || desc.contains("shower") -> WeatherCondition.RAINY
            desc.contains("snow") -> WeatherCondition.SNOWY
            desc.contains("wind") -> WeatherCondition.WINDY
            else -> WeatherCondition.CLEAR
        }
    }
    
    /**
     * 날씨 정보가 시스템에서 사용 가능한지 확인
     */
    fun isSystemWeatherAvailable(): Boolean {
        return isPackageInstalled(SAMSUNG_WEATHER_PACKAGE) || 
               isPackageInstalled(GOOGLE_WEATHER_PACKAGE)
    }
}