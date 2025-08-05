# Build Error Fixes for Galaxy Watch Exercise Trainer App

This document contains all the fixes needed to resolve the compilation errors.

## 1. Fix PlaceRecommendationManager.kt

### Remove duplicate definitions at the end of the file (lines 292-312)
Remove these lines if they exist:
```kotlin
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
```

### Fix ExercisePlace instantiations

1. Around line 165, change:
```kotlin
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
```
to:
```kotlin
ExercisePlace(
    id = "place_${System.currentTimeMillis()}",
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
```

2. Around lines 246-263, add missing parameters:
```kotlin
ExercisePlace(
    id = "offline_park_1",
    name = "동네 공원",
    address = "가까운 공원을 찾아보세요",
    type = ExercisePlaceType.PARK,
    distance = 0f,
    rating = 0f,
    isIndoor = false,
    latitude = 0.0,
    longitude = 0.0,
    recommendationReason = "산책과 조깅에 좋은 장소"
)
```

3. Around lines 255-262, add missing parameters:
```kotlin
ExercisePlace(
    id = "offline_stadium_1",
    name = "학교 운동장",
    address = "근처 학교 운동장",
    type = ExercisePlaceType.STADIUM,
    distance = 0f,
    rating = 0f,
    isIndoor = false,
    latitude = 0.0,
    longitude = 0.0,
    recommendationReason = "트랙 런닝에 적합"
)
```

4. Around line 273, add missing parameters:
```kotlin
ExercisePlace(
    id = "offline_cycle_1",
    name = "자전거 도로",
    address = "한강 자전거 도로",
    type = ExercisePlaceType.CYCLE_PATH,
    distance = 0f,
    rating = 0f,
    isIndoor = false,
    latitude = 0.0,
    longitude = 0.0,
    recommendationReason = "안전한 자전거 주행"
)
```

5. Around line 284, add missing parameters:
```kotlin
ExercisePlace(
    id = "offline_gym_1",
    name = "피트니스 센터",
    address = "근처 헬스장을 검색해보세요",
    type = ExercisePlaceType.GYM,
    distance = 0f,
    rating = 0f,
    isIndoor = true,
    latitude = 0.0,
    longitude = 0.0,
    recommendationReason = "전문 운동 기구 이용 가능"
)
```

## 2. Fix WorkoutRepository.kt

### Add import
Add this import at the top:
```kotlin
import kotlinx.coroutines.flow.first
```

### Fix line 121
Change:
```kotlin
val activeGoals = goalDao.getActiveGoals().value ?: return@withContext
```
to:
```kotlin
val activeGoals = goalDao.getActiveGoals().first()
```

## 3. Fix WeatherDataManager.kt

### Fix line 189
Change:
```kotlin
weather.condition == WeatherCondition.CLEAR && weather.temperature in 15..25 ->
```
to:
```kotlin
weather.condition == WeatherCondition.CLEAR && weather.temperature in 15f..25f ->
```

## 4. Fix all Card components

### AiMessageCard.kt
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter(
        startBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f),
        endBackgroundColor = MaterialTheme.colors.surface
    )
)
```

### GoalProgressCard.kt
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### HealthDataCard.kt
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### PlaceCard.kt
Change:
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() },
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```
to:
```kotlin
Card(
    onClick = onClick,
    modifier = Modifier
        .fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### StatsCard.kt
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### WeatherCard.kt
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### WorkoutHistoryCard.kt
Change:
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() },
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```
to:
```kotlin
Card(
    onClick = onClick,
    modifier = Modifier
        .fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

## 5. Fix Screen components

### GoalsScreen.kt (around line 105)
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### HistoryScreen.kt (around line 129)
Fix the Chip component:
```kotlin
Chip(
    onClick = onClick,
    colors = ChipDefaults.chipColors(
        backgroundColor = if (selected) 
            MaterialTheme.colors.primary 
        else 
            MaterialTheme.colors.surface
    ),
    modifier = Modifier.height(32.dp),
    label = {
        Text(
            text = label,
            style = MaterialTheme.typography.caption2
        )
    }
)
```

### PlacesScreen.kt (around line 67)
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter(
        startBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
        endBackgroundColor = MaterialTheme.colors.surface
    )
)
```

### SettingsScreen.kt (around line 78)
Add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

### WorkoutScreen.kt
1. Around line 108, add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

2. Around line 210, add onClick parameter:
```kotlin
Card(
    onClick = { /* No action */ },
    modifier = Modifier.fillMaxWidth(),
    backgroundPainter = CardDefaults.cardBackgroundPainter()
)
```

## Build Command

After applying all these fixes, run:
```bash
./gradlew clean assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`