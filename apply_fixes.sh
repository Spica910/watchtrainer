#!/bin/bash

# Script to apply all build fixes for Galaxy Watch Exercise Trainer App

echo "Applying build fixes..."

# Fix 1: Remove duplicate definitions from PlaceRecommendationManager.kt
echo "Fixing PlaceRecommendationManager.kt..."
sed -i '292,312d' app/src/main/java/com/watchtrainer/data/PlaceRecommendationManager.kt 2>/dev/null || true

# Fix 2: Update PlaceRecommendationManager ExercisePlace instantiations
# Fix the main ExercisePlace creation
sed -i 's/ExercisePlace(/ExercisePlace(\n                        id = "place_${System.currentTimeMillis()}",/' app/src/main/java/com/watchtrainer/data/PlaceRecommendationManager.kt

# Fix offline recommendations - need to add id, latitude, longitude
sed -i '/ExercisePlace(/,/)/{ 
    s/ExercisePlace(/ExercisePlace(\n                    id = "offline_park_1",/
    s/recommendationReason = "산책과 조깅에 좋은 장소"/latitude = 0.0,\n                    longitude = 0.0,\n                    recommendationReason = "산책과 조깅에 좋은 장소"/
}' app/src/main/java/com/watchtrainer/data/PlaceRecommendationManager.kt

# Fix 3: WorkoutRepository.kt
echo "Fixing WorkoutRepository.kt..."
# Add import
sed -i '/import kotlinx.coroutines.flow.map/a import kotlinx.coroutines.flow.first' app/src/main/java/com/watchtrainer/data/repository/WorkoutRepository.kt
# Fix the value call
sed -i 's/goalDao.getActiveGoals().value ?: return@withContext/goalDao.getActiveGoals().first()/' app/src/main/java/com/watchtrainer/data/repository/WorkoutRepository.kt

# Fix 4: WeatherDataManager.kt
echo "Fixing WeatherDataManager.kt..."
sed -i 's/weather.temperature in 15..25/weather.temperature in 15f..25f/' app/src/main/java/com/watchtrainer/data/WeatherDataManager.kt

# Fix 5: All Card components
echo "Fixing Card components..."
# AiMessageCard.kt
sed -i 's/Card(/Card(\n        onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/components/AiMessageCard.kt

# GoalProgressCard.kt
sed -i 's/Card(/Card(\n        onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/components/GoalProgressCard.kt

# HealthDataCard.kt
sed -i 's/Card(/Card(\n        onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/components/HealthDataCard.kt

# StatsCard.kt
sed -i 's/Card(/Card(\n        onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/components/StatsCard.kt

# WeatherCard.kt
sed -i 's/Card(/Card(\n        onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/components/WeatherCard.kt

# PlaceCard.kt - special case
sed -i 's/.clickable { onClick() }//' app/src/main/java/com/watchtrainer/ui/components/PlaceCard.kt
sed -i 's/Card(/Card(\n        onClick = onClick,/' app/src/main/java/com/watchtrainer/ui/components/PlaceCard.kt

# WorkoutHistoryCard.kt - special case
sed -i 's/.clickable { onClick() }//' app/src/main/java/com/watchtrainer/ui/components/WorkoutHistoryCard.kt
sed -i 's/Card(/Card(\n        onClick = onClick,/' app/src/main/java/com/watchtrainer/ui/components/WorkoutHistoryCard.kt

# Fix 6: Screen components
echo "Fixing Screen components..."
# GoalsScreen.kt
sed -i '/Dialog/,/Card/ s/Card(/Card(\n            onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/screens/GoalsScreen.kt

# PlacesScreen.kt
sed -i '/weatherData?.let/,/Card/ s/Card(/Card(\n                            onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/screens/PlacesScreen.kt

# SettingsScreen.kt
sed -i 's/Card(/Card(\n                    onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/screens/SettingsScreen.kt

# WorkoutScreen.kt - multiple Cards
sed -i '/aiMessage.isNotEmpty/,/Card/ s/Card(/Card(\n                    onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/screens/WorkoutScreen.kt
sed -i '/Box(/,/Card/ s/Card(/Card(\n            onClick = { \/* No action *\/ },/' app/src/main/java/com/watchtrainer/ui/screens/WorkoutScreen.kt

# Fix 7: HistoryScreen Chip
echo "Fixing HistoryScreen Chip..."
sed -i '/private fun PeriodChip/,/^}/ {
    /Chip(/ {
        N
        N
        N
        N
        N
        N
        N
        N
        s/Chip(\n.*onClick = onClick,\n.*colors = ChipDefaults.chipColors(\n.*backgroundColor = if (selected)\n.*MaterialTheme.colors.primary\n.*else\n.*MaterialTheme.colors.surface\n.*),\n.*modifier = Modifier.height(32.dp)\n.*) {/Chip(\n        onClick = onClick,\n        colors = ChipDefaults.chipColors(\n            backgroundColor = if (selected)\n                MaterialTheme.colors.primary\n            else\n                MaterialTheme.colors.surface\n        ),\n        modifier = Modifier.height(32.dp),\n        label = {/
    }
}' app/src/main/java/com/watchtrainer/ui/screens/HistoryScreen.kt

echo "All fixes applied!"
echo "Now run: ./gradlew clean assembleDebug"