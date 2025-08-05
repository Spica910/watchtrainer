package com.watchtrainer.data

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

/**
 * 위치 기반 운동 장소 추천 매니저
 */
class PlaceRecommendationManager(private val context: Context) {
    
    companion object {
        private const val TAG = "PlaceRecommendation"
        private const val GOOGLE_PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/"
        private const val GOOGLE_PLACES_API_KEY = "" // Optional - works with offline recommendations
        
        // 운동 장소 타입 (Google Places API)
        private val OUTDOOR_EXERCISE_TYPES = listOf(
            "park",
            "trail",
            "stadium"
        )
        
        private val INDOOR_EXERCISE_TYPES = listOf(
            "gym",
            "fitness_center",
            "sports_complex"
        )
    }
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    private val placesApi: GooglePlacesApi by lazy {
        Retrofit.Builder()
            .baseUrl(GOOGLE_PLACES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePlacesApi::class.java)
    }
    
    interface GooglePlacesApi {
        @GET("nearbysearch/json")
        suspend fun getNearbyPlaces(
            @Query("location") location: String,
            @Query("radius") radius: Int,
            @Query("type") type: String,
            @Query("key") apiKey: String,
            @Query("language") language: String = "ko"
        ): PlacesResponse
    }
    
    data class PlacesResponse(
        val results: List<Place>,
        val status: String
    )
    
    data class Place(
        val name: String,
        val vicinity: String,
        val geometry: Geometry,
        val types: List<String>,
        val rating: Float? = null,
        val user_ratings_total: Int? = null
    )
    
    data class Geometry(
        val location: LocationData
    )
    
    data class LocationData(
        val lat: Double,
        val lng: Double
    )
    
    /**
     * 현재 날씨와 운동 타입에 따른 추천 장소를 가져옵니다
     */
    suspend fun getRecommendedPlaces(
        weather: WeatherData?,
        workoutType: WorkoutType,
        currentLocation: Location? = null
    ): List<ExercisePlace> = withContext(Dispatchers.IO) {
        try {
            val location = currentLocation ?: getCurrentLocation() ?: return@withContext emptyList()
            val recommendations = mutableListOf<ExercisePlace>()
            
            // 날씨에 따른 실내/실외 결정
            val isIndoorRecommended = shouldRecommendIndoor(weather)
            
            // 운동 타입에 따른 장소 타입 선택
            val placeTypes = when (workoutType) {
                WorkoutType.WALKING, WorkoutType.RUNNING -> {
                    if (isIndoorRecommended) listOf("gym", "fitness_center", "shopping_mall")
                    else listOf("park", "trail", "waterfront")
                }
                WorkoutType.CYCLING -> {
                    if (isIndoorRecommended) listOf("gym", "fitness_center")
                    else listOf("park", "bicycle_track")
                }
                WorkoutType.STRENGTH -> listOf("gym", "fitness_center")
                WorkoutType.YOGA -> listOf("gym", "yoga_studio", "park")
                WorkoutType.OTHER -> OUTDOOR_EXERCISE_TYPES + INDOOR_EXERCISE_TYPES
            }
            
            // API 키가 있으면 실제 검색, 없으면 오프라인 추천
            if (GOOGLE_PLACES_API_KEY.isNotEmpty()) {
                // 각 장소 타입별로 검색
                for (placeType in placeTypes) {
                    val places = searchNearbyPlaces(location, placeType, 2000) // 2km 반경
                    recommendations.addAll(places)
                }
                
                // 거리순으로 정렬하고 중복 제거
                recommendations
                    .distinctBy { it.name }
                    .sortedBy { it.distance }
                    .take(10)
            } else {
                // API 키가 없으면 오프라인 추천
                getOfflineRecommendations(workoutType)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recommended places", e)
            // 오프라인 추천
            getOfflineRecommendations(workoutType)
        }
    }
    
    private suspend fun searchNearbyPlaces(
        location: Location,
        placeType: String,
        radius: Int
    ): List<ExercisePlace> {
        return try {
            val locationStr = "${location.latitude},${location.longitude}"
            val response = placesApi.getNearbyPlaces(
                location = locationStr,
                radius = radius,
                type = placeType,
                apiKey = GOOGLE_PLACES_API_KEY
            )
            
            if (response.status == "OK") {
                response.results.map { place ->
                    val distance = calculateDistance(
                        location.latitude,
                        location.longitude,
                        place.geometry.location.lat,
                        place.geometry.location.lng
                    )
                    
                    ExercisePlace(
                        name = place.name,
                        address = place.vicinity,
                        type = mapPlaceType(place.types),
                        distance = distance,
                        rating = place.rating ?: 0f,
                        isIndoor = isIndoorPlace(place.types),
                        latitude = place.geometry.location.lat,
                        longitude = place.geometry.location.lng,
                        recommendationReason = getRecommendationReason(place, distance)
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching places: $placeType", e)
            emptyList()
        }
    }
    
    private fun shouldRecommendIndoor(weather: WeatherData?): Boolean {
        if (weather == null) return false
        
        return when {
            weather.temperature > 35 -> true // 너무 더움
            weather.temperature < 5 -> true // 너무 추움
            weather.condition == WeatherCondition.RAINY -> true
            weather.condition == WeatherCondition.SNOWY -> true
            weather.windSpeed > 15 -> true // 강풍
            else -> false
        }
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    private fun mapPlaceType(types: List<String>): ExercisePlaceType {
        return when {
            types.contains("park") -> ExercisePlaceType.PARK
            types.contains("gym") || types.contains("fitness_center") -> ExercisePlaceType.GYM
            types.contains("trail") -> ExercisePlaceType.TRAIL
            types.contains("stadium") -> ExercisePlaceType.STADIUM
            types.contains("swimming_pool") -> ExercisePlaceType.POOL
            types.contains("bicycle_track") -> ExercisePlaceType.CYCLE_PATH
            else -> ExercisePlaceType.OTHER
        }
    }
    
    private fun isIndoorPlace(types: List<String>): Boolean {
        return types.any { it in INDOOR_EXERCISE_TYPES }
    }
    
    private fun getRecommendationReason(place: Place, distance: Float): String {
        val reasons = mutableListOf<String>()
        
        if (distance < 500) reasons.add("가까운 거리")
        if (place.rating != null && place.rating >= 4.5) reasons.add("높은 평점")
        if (place.user_ratings_total != null && place.user_ratings_total > 100) reasons.add("인기 장소")
        
        return reasons.joinToString(", ")
    }
    
    private suspend fun getCurrentLocation(): Location? {
        // WeatherDataManager의 getCurrentLocation 메서드 재사용
        return try {
            val weatherManager = WeatherDataManager(context)
            weatherManager.getCurrentLocation()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 오프라인 또는 API 실패 시 기본 추천
     */
    private fun getOfflineRecommendations(workoutType: WorkoutType): List<ExercisePlace> {
        return when (workoutType) {
            WorkoutType.WALKING, WorkoutType.RUNNING -> listOf(
                ExercisePlace(
                    name = "동네 공원",
                    address = "가까운 공원을 찾아보세요",
                    type = ExercisePlaceType.PARK,
                    distance = 0f,
                    rating = 0f,
                    isIndoor = false,
                    recommendationReason = "산책과 조깅에 좋은 장소"
                ),
                ExercisePlace(
                    name = "학교 운동장",
                    address = "근처 학교 운동장",
                    type = ExercisePlaceType.STADIUM,
                    distance = 0f,
                    rating = 0f,
                    isIndoor = false,
                    recommendationReason = "트랙 런닝에 적합"
                )
            )
            WorkoutType.CYCLING -> listOf(
                ExercisePlace(
                    name = "자전거 도로",
                    address = "한강 자전거 도로",
                    type = ExercisePlaceType.CYCLE_PATH,
                    distance = 0f,
                    rating = 0f,
                    isIndoor = false,
                    recommendationReason = "안전한 자전거 주행"
                )
            )
            WorkoutType.STRENGTH, WorkoutType.YOGA -> listOf(
                ExercisePlace(
                    name = "피트니스 센터",
                    address = "근처 헬스장을 검색해보세요",
                    type = ExercisePlaceType.GYM,
                    distance = 0f,
                    rating = 0f,
                    isIndoor = true,
                    recommendationReason = "전문 운동 기구 이용 가능"
                )
            )
            else -> emptyList()
        }
    }
}

data class ExercisePlace(
    val name: String,
    val address: String,
    val type: ExercisePlaceType,
    val distance: Float, // meters
    val rating: Float,
    val isIndoor: Boolean,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val recommendationReason: String = ""
)

enum class ExercisePlaceType {
    PARK,
    GYM,
    TRAIL,
    STADIUM,
    POOL,
    CYCLE_PATH,
    OTHER
}