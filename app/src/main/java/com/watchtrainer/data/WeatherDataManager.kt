package com.watchtrainer.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.os.Build
import android.provider.Settings

class WeatherDataManager(private val context: Context) {
    
    companion object {
        private const val TAG = "WeatherDataManager"
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val API_KEY = "" // Optional - works without API key using system weather
    }
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    private val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
    
    interface WeatherApi {
        @GET("weather")
        suspend fun getWeather(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "kr"
        ): WeatherResponse
    }
    
    data class WeatherResponse(
        val main: Main,
        val weather: List<Weather>,
        val wind: Wind
    )
    
    data class Main(
        val temp: Float,
        val feels_like: Float,
        val humidity: Int
    )
    
    data class Weather(
        val main: String,
        val description: String
    )
    
    data class Wind(
        val speed: Float
    )
    
    fun getWeatherFlow(): Flow<WeatherData> = flow {
        while (true) {
            try {
                val weatherData = getCurrentWeather()
                emit(weatherData)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get weather data", e)
            }
            
            // Update every 30 minutes
            kotlinx.coroutines.delay(30 * 60 * 1000)
        }
    }
    
    suspend fun getCurrentWeather(): WeatherData = withContext(Dispatchers.IO) {
        try {
            // 1. Try system weather first (Wear OS)
            val systemWeatherManager = SystemWeatherManager(context)
            systemWeatherManager.getSystemWeatherData()?.let { 
                Log.d(TAG, "Using system weather data")
                return@withContext it 
            }
            
            // 2. Fall back to API if system weather not available and API key is provided
            if (API_KEY.isNotEmpty()) {
                Log.d(TAG, "System weather not available, using API")
                val location = getCurrentLocation()
                val response = weatherApi.getWeather(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    apiKey = API_KEY
                )
                
                WeatherData(
                    temperature = response.main.temp,
                    description = response.weather.firstOrNull()?.description ?: "",
                    humidity = response.main.humidity,
                    windSpeed = response.wind.speed,
                    feelsLike = response.main.feels_like,
                    condition = mapWeatherCondition(response.weather.firstOrNull()?.main ?: "")
                )
            } else {
                // Return default weather data when no API key
                Log.d(TAG, "No weather API key, using default data")
                getDefaultWeatherData()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather", e)
            // Return default weather data
            WeatherData()
        }
    }
    
    suspend fun getCurrentLocation(): Location = suspendCancellableCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resumeWithException(SecurityException("Location permission not granted"))
            return@suspendCancellableCoroutine
        }
        
        val cancellationToken = CancellationTokenSource()
        
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                continuation.resume(location)
            } else {
                // Try last known location
                fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                    if (lastLocation != null) {
                        continuation.resume(lastLocation)
                    } else {
                        // Default location (Seoul)
                        val defaultLocation = Location("").apply {
                            latitude = 37.5665
                            longitude = 126.9780
                        }
                        continuation.resume(defaultLocation)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        
        continuation.invokeOnCancellation {
            cancellationToken.cancel()
        }
    }
    
    private fun mapWeatherCondition(weatherMain: String): WeatherCondition {
        return when (weatherMain.lowercase()) {
            "clear" -> WeatherCondition.CLEAR
            "clouds" -> WeatherCondition.CLOUDY
            "rain", "drizzle" -> WeatherCondition.RAINY
            "snow" -> WeatherCondition.SNOWY
            "wind" -> WeatherCondition.WINDY
            else -> WeatherCondition.CLEAR
        }
    }
    
    fun getWeatherRecommendation(weather: WeatherData): String {
        return when {
            weather.temperature > 30 -> "너무 더워요! 실내 운동을 추천합니다. 🏠"
            weather.temperature < 5 -> "추워요! 따뜻하게 입고 운동하세요. 🧤"
            weather.condition == WeatherCondition.RAINY -> "비가 와요! 실내 운동이나 우산을 준비하세요. ☔"
            weather.condition == WeatherCondition.CLEAR && weather.temperature in 15..25 -> 
                "완벽한 날씨예요! 야외 운동을 즐기세요! 🌞"
            weather.windSpeed > 10 -> "바람이 강해요! 실내 운동을 고려해보세요. 💨"
            else -> "운동하기 좋은 날씨네요! 💪"
        }
    }
    
    private fun getDefaultWeatherData(): WeatherData {
        // Return reasonable default weather data
        return WeatherData(
            temperature = 20f,
            description = "맑음",
            humidity = 50,
            windSpeed = 5f,
            feelsLike = 20f,
            condition = WeatherCondition.CLEAR
        )
    }
}