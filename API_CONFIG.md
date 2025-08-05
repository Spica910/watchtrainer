# API 설정 가이드

## 🔑 필수 API (1개)

### Gemini API (Google AI)
**용도**: AI 운동 코치, 맞춤형 가이드
**설정 위치**: `app/src/main/java/com/watchtrainer/data/GeminiAIManager.kt`

```kotlin
private const val API_KEY = "YOUR_GEMINI_API_KEY" // 여기에 키 입력
```

**키 받는 방법**:
1. https://makersuite.google.com/app/apikey 접속
2. Google 계정으로 로그인
3. "Create API Key" 클릭
4. 생성된 키 복사

## 🌟 선택적 API (없어도 앱 작동)

### 1. OpenWeather API
**용도**: 날씨 정보 (시스템 날씨가 없을 때만)
**설정 위치**: `app/src/main/java/com/watchtrainer/data/WeatherDataManager.kt`

```kotlin
private const val API_KEY = "" // 비워두면 시스템 날씨 사용
```

### 2. Google Places API  
**용도**: 주변 운동 장소 검색
**설정 위치**: `app/src/main/java/com/watchtrainer/data/PlaceRecommendationManager.kt`

```kotlin
private const val GOOGLE_PLACES_API_KEY = "" // 비워두면 오프라인 추천
```

## 📌 API별 기능 비교

| 기능 | Gemini API 필요 | 다른 API 필요 | API 없을 때 |
|------|----------------|--------------|------------|
| AI 코치 메시지 | ✅ 필수 | - | 기본 메시지 |
| 운동 가이드 | ✅ 필수 | - | 기본 가이드 |
| 날씨 정보 | ❌ | OpenWeather (선택) | 시스템 날씨/기본값 |
| 장소 추천 | ❌ | Google Places (선택) | 일반 추천 |
| 운동 추적 | ❌ | - | ✅ 정상 작동 |
| 음성 안내 | ❌ | - | ✅ 정상 작동 |
| 목표 설정 | ❌ | - | ✅ 정상 작동 |

## 🎯 권장 설정

### 최소 설정 (핵심 기능만)
- Gemini API 키만 설정
- 나머지는 비워두기

### 전체 기능
- 모든 API 키 설정
- 실시간 날씨, 주변 장소 검색 가능

## ⚡ 빠른 테스트

Gemini API만 설정하고 바로 사용 가능:
```kotlin
// GeminiAIManager.kt
private const val API_KEY = "실제_gemini_api_키_입력"

// 나머지는 그대로 두기
// WeatherDataManager.kt
private const val API_KEY = "" // 비워두면 시스템 날씨 사용

// PlaceRecommendationManager.kt  
private const val GOOGLE_PLACES_API_KEY = "" // 비워두면 오프라인 추천
```