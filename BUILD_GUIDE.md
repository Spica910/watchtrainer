# 갤럭시 워치 운동 트레이너 앱 빌드 가이드

## 빌드 전 준비사항

### 1. Android SDK 설정
`local.properties` 파일에서 SDK 경로를 설정하세요:
```
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### 2. API 키 설정
다음 파일들에서 API 키를 실제 값으로 변경하세요:

- **WeatherDataManager.kt**
  ```kotlin
  private const val API_KEY = "YOUR_OPENWEATHER_API_KEY"
  ```

- **GeminiAIManager.kt**
  ```kotlin
  private const val API_KEY = "YOUR_GEMINI_API_KEY"
  ```

- **PlaceRecommendationManager.kt**
  ```kotlin
  private const val GOOGLE_PLACES_API_KEY = "YOUR_GOOGLE_PLACES_API_KEY"
  ```

### 3. Samsung Health SDK
Samsung Health SDK AAR 파일을 다운로드하여 `app/libs/` 디렉토리에 추가한 후, `app/build.gradle.kts`에서 주석을 해제하세요:
```kotlin
implementation(files("libs/samsung-health-data-api-1.5.0.aar"))
```

## 빌드 명령어

### 1. 디버그 빌드
```bash
./gradlew assembleDebug
```

### 2. 릴리즈 빌드
```bash
./gradlew assembleRelease
```

### 3. 전체 빌드 및 테스트
```bash
./gradlew build
```

### 4. 기기에 설치
```bash
./gradlew installDebug
```

## 프로젝트 구조

```
watchtrainer/
├── app/
│   ├── src/main/java/com/watchtrainer/
│   │   ├── MainActivity.kt                 # 메인 액티비티
│   │   ├── data/                          # 데이터 관리
│   │   │   ├── Models.kt                  # 데이터 모델
│   │   │   ├── HealthDataManager.kt       # 삼성 헬스 연동
│   │   │   ├── WeatherDataManager.kt      # 날씨 데이터
│   │   │   ├── SystemWeatherManager.kt    # 시스템 날씨
│   │   │   ├── PlaceRecommendationManager.kt # 장소 추천
│   │   │   ├── GeminiAIManager.kt         # AI 코치
│   │   │   ├── database/                  # Room DB
│   │   │   └── repository/               # 데이터 저장소
│   │   ├── ui/
│   │   │   ├── screens/                  # 화면들
│   │   │   │   ├── HomeScreen.kt         # 홈 화면
│   │   │   │   ├── WorkoutScreen.kt      # 운동 화면
│   │   │   │   ├── HistoryScreen.kt      # 기록 화면
│   │   │   │   ├── GoalsScreen.kt        # 목표 화면
│   │   │   │   ├── PlacesScreen.kt       # 장소 추천 화면
│   │   │   │   └── SettingsScreen.kt     # 설정 화면
│   │   │   ├── components/               # UI 컴포넌트
│   │   │   └── theme/                    # 테마 설정
│   │   ├── services/
│   │   │   └── ExerciseService.kt        # 운동 추적 서비스
│   │   ├── utils/
│   │   │   └── TextToSpeechManager.kt    # 음성 피드백
│   │   └── viewmodels/                   # 뷰모델
│   └── src/main/res/                     # 리소스
├── build.gradle.kts                       # 프로젝트 빌드 설정
├── settings.gradle.kts                    # 프로젝트 설정
└── gradle.properties                      # Gradle 속성

```

## 주요 파일 (32개 Kotlin 파일)

### 핵심 기능
- MainActivity.kt - 메인 진입점, 하드웨어 버튼 처리
- MainViewModel.kt - 전체 앱 상태 관리
- ExerciseService.kt - 백그라운드 운동 추적

### 데이터 관리
- HealthDataManager.kt - 삼성 헬스 데이터 연동
- WeatherDataManager.kt - 날씨 API 연동
- SystemWeatherManager.kt - Wear OS 시스템 날씨
- PlaceRecommendationManager.kt - 위치 기반 장소 추천
- GeminiAIManager.kt - AI 코치 기능
- TextToSpeechManager.kt - 음성 피드백

### UI 화면 (6개)
- HomeScreen.kt - 메인 화면
- WorkoutScreen.kt - 운동 진행 화면
- HistoryScreen.kt - 운동 기록 조회
- GoalsScreen.kt - 목표 설정/관리
- PlacesScreen.kt - 추천 장소 목록
- SettingsScreen.kt - 앱 설정

### 데이터베이스
- WorkoutDatabase.kt - Room 데이터베이스
- WorkoutRepository.kt - 데이터 저장소

## 문제 해결

### SDK 위치 오류
```
SDK location not found. Define a valid SDK location...
```
해결: `local.properties` 파일에 올바른 SDK 경로 설정

### 빌드 실패
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### 권한 오류
AndroidManifest.xml에 필요한 권한이 모두 포함되어 있는지 확인

## 테스트

### 에뮬레이터에서 실행
1. Android Studio에서 Wear OS 에뮬레이터 생성
2. API Level 30 이상 선택
3. `./gradlew installDebug` 실행

### 실제 기기에서 실행
1. 갤럭시 워치 개발자 모드 활성화
2. ADB 디버깅 활성화
3. `./gradlew installDebug` 실행